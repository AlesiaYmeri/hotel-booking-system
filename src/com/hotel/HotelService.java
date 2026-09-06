package com.hotel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HotelService {

    private final Path dataDir;
    private final Path roomsFile;
    private final Path bookingsFile;

    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    private final AtomicInteger nextRoomId = new AtomicInteger(1);
    private final AtomicInteger nextBookingId = new AtomicInteger(1);

    public HotelService(String dataDirPath) {
        this.dataDir = Paths.get(dataDirPath);
        this.roomsFile = dataDir.resolve("rooms.csv");
        this.bookingsFile = dataDir.resolve("bookings.csv");
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize data storage: " + e.getMessage(), e);
        }
    }

    private synchronized void init() throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        if (!Files.exists(roomsFile)) {
            seedRooms();
            saveRooms();
        } else {
            loadRooms();
        }
        if (!Files.exists(bookingsFile)) {
            Files.createFile(bookingsFile);
        } else {
            loadBookings();
        }
    }

    private void seedRooms() {
        rooms.add(new Room(nextRoomId.getAndIncrement(), "101", RoomType.SINGLE, 45.0));
        rooms.add(new Room(nextRoomId.getAndIncrement(), "102", RoomType.SINGLE, 45.0));
        rooms.add(new Room(nextRoomId.getAndIncrement(), "201", RoomType.DOUBLE, 70.0));
        rooms.add(new Room(nextRoomId.getAndIncrement(), "202", RoomType.DOUBLE, 75.0));
        rooms.add(new Room(nextRoomId.getAndIncrement(), "301", RoomType.SUITE, 130.0));
        rooms.add(new Room(nextRoomId.getAndIncrement(), "401", RoomType.DELUXE, 190.0));
    }

    private void loadRooms() throws IOException {
        List<String> lines = Files.readAllLines(roomsFile, StandardCharsets.UTF_8);
        int maxId = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            Room r = Room.fromCsvLine(line);
            rooms.add(r);
            maxId = Math.max(maxId, r.getId());
        }
        nextRoomId.set(maxId + 1);
    }

    private void loadBookings() throws IOException {
        List<String> lines = Files.readAllLines(bookingsFile, StandardCharsets.UTF_8);
        int maxId = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            Booking b = Booking.fromCsvLine(line);
            bookings.add(b);
            maxId = Math.max(maxId, b.getId());
        }
        nextBookingId.set(maxId + 1);
    }

    private void saveRooms() {
        try {
            List<String> lines = rooms.stream().map(Room::toCsvLine).collect(Collectors.toList());
            Files.write(roomsFile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save rooms: " + e.getMessage(), e);
        }
    }

    private void saveBookings() {
        try {
            List<String> lines = bookings.stream().map(Booking::toCsvLine).collect(Collectors.toList());
            Files.write(bookingsFile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bookings: " + e.getMessage(), e);
        }
    }


    public synchronized List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public synchronized Optional<Room> getRoomById(int id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst();
    }

    public synchronized Room addRoom(String number, RoomType type, double pricePerNight) {
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required.");
        }
        boolean duplicate = rooms.stream().anyMatch(r -> r.getNumber().equalsIgnoreCase(number.trim()));
        if (duplicate) {
            throw new IllegalArgumentException("A room with number " + number + " already exists.");
        }
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("Price per night must be greater than zero.");
        }
        Room room = new Room(nextRoomId.getAndIncrement(), number.trim(), type, pricePerNight);
        rooms.add(room);
        saveRooms();
        return room;
    }

    public synchronized void deleteRoom(int roomId) {
        boolean hasActiveBooking = bookings.stream()
                .anyMatch(b -> b.getRoomId() == roomId && b.isActive());
        if (hasActiveBooking) {
            throw new IllegalStateException("Cannot delete a room that has active bookings.");
        }
        rooms.removeIf(r -> r.getId() == roomId);
        saveRooms();
    }

    public synchronized boolean isRoomOccupiedToday(int roomId) {
        LocalDate today = LocalDate.now();
        return bookings.stream().anyMatch(b -> b.getRoomId() == roomId && b.isActive()
                && !today.isBefore(b.getCheckIn()) && today.isBefore(b.getCheckOut()));
    }


    public synchronized List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public synchronized List<Booking> getActiveBookings() {
        return bookings.stream().filter(Booking::isActive)
                .sorted((a, b) -> a.getCheckIn().compareTo(b.getCheckIn()))
                .collect(Collectors.toList());
    }

    public synchronized List<Booking> getBookingsForRoom(int roomId) {
        return bookings.stream().filter(b -> b.getRoomId() == roomId).collect(Collectors.toList());
    }

    public synchronized boolean isAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookings.stream()
                .filter(b -> b.getRoomId() == roomId && b.isActive())
                .noneMatch(b -> b.overlaps(checkIn, checkOut));
    }

    public synchronized Booking createBooking(int roomId, String guestName, String guestEmail,
                                               LocalDate checkIn, LocalDate checkOut) {
        Room room = getRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name is required.");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!isAvailable(roomId, checkIn, checkOut)) {
            throw new IllegalArgumentException("Room " + room.getNumber()
                    + " is already booked for the selected dates.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = nights * room.getPricePerNight();

        Booking booking = new Booking(
                nextBookingId.getAndIncrement(),
                roomId,
                guestName.trim(),
                guestEmail == null ? "" : guestEmail.trim(),
                checkIn,
                checkOut,
                totalPrice,
                BookingStatus.ACTIVE,
                LocalDateTime.now().withNano(0)
        );
        bookings.add(booking);
        saveBookings();
        return booking;
    }

    public synchronized void cancelBooking(int bookingId) {
        Booking booking = bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        if (!booking.isActive()) {
            throw new IllegalStateException("This booking is already cancelled.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        saveBookings();
    }

    public synchronized double getTotalRevenue() {
        return bookings.stream().filter(Booking::isActive).mapToDouble(Booking::getTotalPrice).sum();
    }
}
