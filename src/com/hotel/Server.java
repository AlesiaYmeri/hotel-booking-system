package com.hotel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 8080;
    private final HotelService service;

    public Server(HotelService service) {
        this.service = service;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", this::handleHome);
        server.createContext("/style.css", this::handleStyle);
        server.createContext("/book", this::handleBook);
        server.createContext("/cancel", this::handleCancel);
        server.createContext("/rooms/new", this::handleRoomNew);
        server.createContext("/rooms/delete", this::handleRoomDelete);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("=================================================");
        System.out.println(" Hotel Booking System is running!");
        System.out.println(" Open your browser at: http://localhost:" + PORT);
        System.out.println(" Press Ctrl+C to stop the server.");
        System.out.println("=================================================");
    }

    private void handleHome(HttpExchange exchange) throws IOException {
        if (!isMethod(exchange, "GET")) {
            sendMethodNotAllowed(exchange);
            return;
        }

        if (!exchange.getRequestURI().getPath().equals("/")) {
            sendNotFound(exchange);
            return;
        }
        Map<String, String> params = HttpUtils.parseParams(exchange.getRequestURI().getRawQuery());
        String msg = params.get("msg");
        String type = params.getOrDefault("type", "success");
        String html = View.dashboard(service, msg, type);
        sendHtml(exchange, 200, html);
    }

    private void handleStyle(HttpExchange exchange) throws IOException {
        if (!isMethod(exchange, "GET")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        byte[] bytes = Css.CONTENT.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/css; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleBook(HttpExchange exchange) throws IOException {
        if (isMethod(exchange, "GET")) {
            Map<String, String> params = HttpUtils.parseParams(exchange.getRequestURI().getRawQuery());
            String roomIdStr = params.get("roomId");
            Optional<Room> room = tryParseRoom(roomIdStr);
            if (room.isEmpty()) {
                redirect(exchange, "/?msg=" + encode("Please choose a valid room first.") + "&type=error");
                return;
            }
            sendHtml(exchange, 200, View.bookingForm(room.get(), null));
            return;
        }

        if (isMethod(exchange, "POST")) {
            Map<String, String> form = HttpUtils.parseParams(HttpUtils.readBody(exchange.getRequestBody()));
            String roomIdStr = form.getOrDefault("roomId", "");
            try {
                int roomId = Integer.parseInt(roomIdStr);
                String guestName = form.getOrDefault("guestName", "");
                String guestEmail = form.getOrDefault("guestEmail", "");
                LocalDate checkIn = LocalDate.parse(form.getOrDefault("checkIn", ""));
                LocalDate checkOut = LocalDate.parse(form.getOrDefault("checkOut", ""));

                Booking booking = service.createBooking(roomId, guestName, guestEmail, checkIn, checkOut);
                String roomLabel = service.getRoomById(roomId).map(Room::getNumber).orElse(String.valueOf(roomId));
                String successMsg = "Booking confirmed for Room " + roomLabel + ". Total: $"
                        + String.format("%.2f", booking.getTotalPrice());
                redirect(exchange, "/?msg=" + encode(successMsg) + "&type=success");

            } catch (DateTimeParseException e) {
                respondWithBookingError(exchange, roomIdStr, "Please provide valid check-in and check-out dates.");
            } catch (NumberFormatException e) {
                redirect(exchange, "/?msg=" + encode("Invalid room selection.") + "&type=error");
            } catch (IllegalArgumentException | IllegalStateException e) {
                respondWithBookingError(exchange, roomIdStr, e.getMessage());
            }
            return;
        }

        sendMethodNotAllowed(exchange);
    }

    private void respondWithBookingError(HttpExchange exchange, String roomIdStr, String message) throws IOException {
        Optional<Room> room = tryParseRoom(roomIdStr);
        if (room.isPresent()) {
            sendHtml(exchange, 200, View.bookingForm(room.get(), message));
        } else {
            redirect(exchange, "/?msg=" + encode(message == null ? "Something went wrong." : message) + "&type=error");
        }
    }

    private void handleCancel(HttpExchange exchange) throws IOException {
        if (!isMethod(exchange, "POST")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        Map<String, String> form = HttpUtils.parseParams(HttpUtils.readBody(exchange.getRequestBody()));
        try {
            int bookingId = Integer.parseInt(form.getOrDefault("bookingId", ""));
            service.cancelBooking(bookingId);
            redirect(exchange, "/?msg=" + encode("Booking cancelled successfully.") + "&type=success");
        } catch (NumberFormatException e) {
            redirect(exchange, "/?msg=" + encode("Invalid booking id.") + "&type=error");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect(exchange, "/?msg=" + encode(e.getMessage()) + "&type=error");
        }
    }

    private void handleRoomNew(HttpExchange exchange) throws IOException {
        if (isMethod(exchange, "GET")) {
            sendHtml(exchange, 200, View.roomForm(null));
            return;
        }

        if (isMethod(exchange, "POST")) {
            Map<String, String> form = HttpUtils.parseParams(HttpUtils.readBody(exchange.getRequestBody()));
            try {
                String number = form.getOrDefault("number", "");
                RoomType type = RoomType.fromString(form.getOrDefault("type", "SINGLE"));
                double price = Double.parseDouble(form.getOrDefault("price", "0"));
                service.addRoom(number, type, price);
                redirect(exchange, "/?msg=" + encode("Room " + number + " added successfully.") + "&type=success");
            } catch (NumberFormatException e) {
                sendHtml(exchange, 200, View.roomForm("Please enter a valid price."));
            } catch (IllegalArgumentException e) {
                sendHtml(exchange, 200, View.roomForm(e.getMessage()));
            }
            return;
        }

        sendMethodNotAllowed(exchange);
    }

    private void handleRoomDelete(HttpExchange exchange) throws IOException {
        if (!isMethod(exchange, "POST")) {
            sendMethodNotAllowed(exchange);
            return;
        }
        Map<String, String> form = HttpUtils.parseParams(HttpUtils.readBody(exchange.getRequestBody()));
        try {
            int roomId = Integer.parseInt(form.getOrDefault("roomId", ""));
            service.deleteRoom(roomId);
            redirect(exchange, "/?msg=" + encode("Room removed.") + "&type=success");
        } catch (NumberFormatException e) {
            redirect(exchange, "/?msg=" + encode("Invalid room id.") + "&type=error");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect(exchange, "/?msg=" + encode(e.getMessage()) + "&type=error");
        }
    }


    private Optional<Room> tryParseRoom(String roomIdStr) {
        if (roomIdStr == null) {
            return Optional.empty();
        }
        try {
            int roomId = Integer.parseInt(roomIdStr);
            return service.getRoomById(roomId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private boolean isMethod(HttpExchange exchange, String method) {
        return exchange.getRequestMethod().equalsIgnoreCase(method);
    }

    private void sendHtml(HttpExchange exchange, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().add("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void sendNotFound(HttpExchange exchange) throws IOException {
        String html = "<h1>404 - Page Not Found</h1><p><a href=\"/\">Go back to the dashboard</a></p>";
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(404, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String html = "<h1>405 - Method Not Allowed</h1><p><a href=\"/\">Go back to the dashboard</a></p>";
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(405, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    public static void main(String[] args) throws IOException {
        HotelService service = new HotelService("data");
        Server server = new Server(service);
        server.start();
    }
}
