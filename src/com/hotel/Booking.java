package com.hotel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private final int id;
    private final int roomId;
    private String guestName;
    private String guestEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalPrice;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(int id, int roomId, String guestName, String guestEmail,
                    LocalDate checkIn, LocalDate checkOut, double totalPrice,
                    BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    public boolean overlaps(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return checkIn.isBefore(otherCheckOut) && otherCheckIn.isBefore(checkOut);
    }

    public String toCsvLine() {
        String emailSafe = (guestEmail == null || guestEmail.isEmpty()) ? "-" : guestEmail;
        return id + "|" + roomId + "|" + guestName + "|" + emailSafe + "|"
                + checkIn + "|" + checkOut + "|" + totalPrice + "|" + status.name() + "|" + createdAt;
    }

    public static Booking fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        int id = Integer.parseInt(parts[0].trim());
        int roomId = Integer.parseInt(parts[1].trim());
        String guestName = parts[2].trim();
        String guestEmail = parts[3].trim();
        if (guestEmail.equals("-")) {
            guestEmail = "";
        }
        LocalDate checkIn = LocalDate.parse(parts[4].trim());
        LocalDate checkOut = LocalDate.parse(parts[5].trim());
        double totalPrice = Double.parseDouble(parts[6].trim());
        BookingStatus status = BookingStatus.valueOf(parts[7].trim());
        LocalDateTime createdAt = LocalDateTime.parse(parts[8].trim());
        return new Booking(id, roomId, guestName, guestEmail, checkIn, checkOut, totalPrice, status, createdAt);
    }
}
