package com.hotel;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public final class View {

    private static final DateTimeFormatter NICE_DATE =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    private View() {
    }


    private static String page(String title, String bodyContent) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>" + HttpUtils.escapeHtml(title) + " · Hotel Booking System</title>\n" +
                "  <link rel=\"stylesheet\" href=\"/style.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "  <header class=\"topbar\">\n" +
                "    <h1>&#127976; Hotel Booking System</h1>\n" +
                "    <p>Rooms &middot; Reservations &middot; Cancellations, all in one place.</p>\n" +
                "    <nav class=\"tabs\">\n" +
                "      <a href=\"/\">Dashboard</a>\n" +
                "      <a href=\"/rooms/new\">Add Room</a>\n" +
                "    </nav>\n" +
                "  </header>\n" +
                "  <main>\n" +
                bodyContent +
                "  </main>\n" +
                "  <footer>Hotel Booking System &mdash; data is saved to disk automatically.</footer>\n" +
                "</body>\n" +
                "</html>\n";
    }

    private static String flash(String msg, String type) {
        if (msg == null || msg.isEmpty()) {
            return "";
        }
        String cls = "error".equals(type) ? "error" : "success";
        return "<div class=\"flash " + cls + "\">" + HttpUtils.escapeHtml(msg) + "</div>\n";
    }

    public static String dashboard(HotelService service, String msg, String type) {
        List<Room> rooms = service.getAllRooms();
        List<Booking> activeBookings = service.getActiveBookings();
        double revenue = service.getTotalRevenue();

        StringBuilder sb = new StringBuilder();
        sb.append(flash(msg, type));

        // Summary bar
        sb.append("<div class=\"summary-bar\">\n");
        sb.append(summaryItem("Total Rooms", String.valueOf(rooms.size())));
        sb.append(summaryItem("Active Bookings", String.valueOf(activeBookings.size())));
        sb.append(summaryItem("Confirmed Revenue", String.format(Locale.ENGLISH, "$%,.2f", revenue)));
        sb.append("</div>\n");

        // Rooms section
        sb.append("<section>\n");
        sb.append("<h2>Rooms <span class=\"count\">").append(rooms.size()).append("</span></h2>\n");
        if (rooms.isEmpty()) {
            sb.append("<div class=\"empty\">No rooms yet. <a href=\"/rooms/new\">Add the first room</a>.</div>\n");
        } else {
            sb.append("<div class=\"grid\">\n");
            for (Room room : rooms) {
                sb.append(roomCard(service, room));
            }
            sb.append("</div>\n");
        }
        sb.append("</section>\n");

        // Bookings section
        sb.append("<section>\n");
        sb.append("<h2>Active Bookings <span class=\"count\">").append(activeBookings.size()).append("</span></h2>\n");
        if (activeBookings.isEmpty()) {
            sb.append("<div class=\"empty\">No active bookings at the moment.</div>\n");
        } else {
            sb.append("<table>\n<thead><tr>")
              .append("<th>Guest</th><th>Room</th><th>Check-in</th><th>Check-out</th>")
              .append("<th>Nights</th><th>Total</th><th></th>")
              .append("</tr></thead>\n<tbody>\n");
            for (Booking b : activeBookings) {
                sb.append(bookingRow(service, b));
            }
            sb.append("</tbody>\n</table>\n");
        }
        sb.append("</section>\n");

        return page("Dashboard", sb.toString());
    }

    private static String summaryItem(String label, String value) {
        return "<div class=\"summary-item\"><div class=\"label\">" + HttpUtils.escapeHtml(label)
                + "</div><div class=\"value\">" + HttpUtils.escapeHtml(value) + "</div></div>\n";
    }

    private static String roomCard(HotelService service, Room room) {
        boolean occupiedToday = service.isRoomOccupiedToday(room.getId());
        String statusClass = occupiedToday ? "occupied" : "free";
        String statusLabel = occupiedToday ? "Occupied today" : "Free today";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"card room-card\">\n");
        sb.append("<div class=\"room-type\">").append(room.getType().getLabel()).append("</div>\n");
        sb.append("<h3>Room ").append(HttpUtils.escapeHtml(room.getNumber())).append("</h3>\n");
        sb.append("<div class=\"status ").append(statusClass).append("\">").append(statusLabel).append("</div>\n");
        sb.append("<div class=\"price\">$").append(String.format(Locale.ENGLISH, "%,.2f", room.getPricePerNight()))
          .append(" <small>/ night</small></div>\n");
        sb.append("<a class=\"btn block\" href=\"/book?roomId=").append(room.getId()).append("\">Reserve</a>\n");
        sb.append("<form method=\"post\" action=\"/rooms/delete\" style=\"margin-top:8px;\" ")
          .append("onsubmit=\"return confirm('Remove room ").append(HttpUtils.escapeHtml(room.getNumber()))
          .append("? This cannot be undone.');\">\n");
        sb.append("<input type=\"hidden\" name=\"roomId\" value=\"").append(room.getId()).append("\">\n");
        sb.append("<button type=\"submit\" class=\"btn secondary block\">Remove</button>\n");
        sb.append("</form>\n");
        sb.append("</div>\n");
        return sb.toString();
    }

    private static String bookingRow(HotelService service, Booking b) {
        String roomLabel = service.getRoomById(b.getRoomId())
                .map(r -> "Room " + r.getNumber())
                .orElse("Room #" + b.getRoomId());
        long nights = ChronoUnit.DAYS.between(b.getCheckIn(), b.getCheckOut());

        StringBuilder sb = new StringBuilder();
        sb.append("<tr>\n");
        sb.append("<td data-label=\"Guest\">").append(HttpUtils.escapeHtml(b.getGuestName()));
        if (b.getGuestEmail() != null && !b.getGuestEmail().isEmpty()) {
            sb.append("<br><small style=\"color:var(--muted);\">")
              .append(HttpUtils.escapeHtml(b.getGuestEmail())).append("</small>");
        }
        sb.append("</td>\n");
        sb.append("<td data-label=\"Room\">").append(HttpUtils.escapeHtml(roomLabel)).append("</td>\n");
        sb.append("<td data-label=\"Check-in\">").append(b.getCheckIn().format(NICE_DATE)).append("</td>\n");
        sb.append("<td data-label=\"Check-out\">").append(b.getCheckOut().format(NICE_DATE)).append("</td>\n");
        sb.append("<td data-label=\"Nights\">").append(nights).append("</td>\n");
        sb.append("<td data-label=\"Total\">$").append(String.format(Locale.ENGLISH, "%,.2f", b.getTotalPrice())).append("</td>\n");
        sb.append("<td data-label=\"Action\">\n");
        sb.append("<form method=\"post\" action=\"/cancel\" onsubmit=\"return confirm('Cancel this booking?');\">\n");
        sb.append("<input type=\"hidden\" name=\"bookingId\" value=\"").append(b.getId()).append("\">\n");
        sb.append("<button type=\"submit\" class=\"btn danger\">Cancel</button>\n");
        sb.append("</form>\n");
        sb.append("</td>\n");
        sb.append("</tr>\n");
        return sb.toString();
    }


    public static String bookingForm(Room room, String errorMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a class=\"back-link\" href=\"/\">&larr; Back to dashboard</a>\n");
        sb.append("<div class=\"card form-card\">\n");
        sb.append("<h2>Reserve Room ").append(HttpUtils.escapeHtml(room.getNumber())).append("</h2>\n");
        sb.append("<p style=\"color:var(--muted);margin-top:-8px;\">")
          .append(room.getType().getLabel()).append(" &middot; $")
          .append(String.format(Locale.ENGLISH, "%,.2f", room.getPricePerNight())).append(" / night</p>\n");

        if (errorMessage != null && !errorMessage.isEmpty()) {
            sb.append(flash(errorMessage, "error"));
        }

        sb.append("<form method=\"post\" action=\"/book\">\n");
        sb.append("<input type=\"hidden\" name=\"roomId\" value=\"").append(room.getId()).append("\">\n");
        sb.append("<label for=\"guestName\">Guest name</label>\n");
        sb.append("<input type=\"text\" id=\"guestName\" name=\"guestName\" placeholder=\"e.g. John Smith\" required>\n");
        sb.append("<label for=\"guestEmail\">Email (optional)</label>\n");
        sb.append("<input type=\"email\" id=\"guestEmail\" name=\"guestEmail\" placeholder=\"e.g. john@example.com\">\n");
        sb.append("<label for=\"checkIn\">Check-in date</label>\n");
        sb.append("<input type=\"date\" id=\"checkIn\" name=\"checkIn\" required>\n");
        sb.append("<label for=\"checkOut\">Check-out date</label>\n");
        sb.append("<input type=\"date\" id=\"checkOut\" name=\"checkOut\" required>\n");
        sb.append("<div class=\"row-actions\">\n");
        sb.append("<button type=\"submit\" class=\"btn\">Confirm Reservation</button>\n");
        sb.append("<a class=\"btn secondary\" href=\"/\">Cancel</a>\n");
        sb.append("</div>\n");
        sb.append("</form>\n");
        sb.append("</div>\n");
        return page("Reserve Room", sb.toString());
    }


    public static String roomForm(String errorMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a class=\"back-link\" href=\"/\">&larr; Back to dashboard</a>\n");
        sb.append("<div class=\"card form-card\">\n");
        sb.append("<h2>Add a New Room</h2>\n");

        if (errorMessage != null && !errorMessage.isEmpty()) {
            sb.append(flash(errorMessage, "error"));
        }

        sb.append("<form method=\"post\" action=\"/rooms/new\">\n");
        sb.append("<label for=\"number\">Room number</label>\n");
        sb.append("<input type=\"text\" id=\"number\" name=\"number\" placeholder=\"e.g. 501\" required>\n");
        sb.append("<label for=\"type\">Room type</label>\n");
        sb.append("<select id=\"type\" name=\"type\">\n");
        for (RoomType t : RoomType.values()) {
            sb.append("<option value=\"").append(t.name()).append("\">").append(t.getLabel()).append("</option>\n");
        }
        sb.append("</select>\n");
        sb.append("<label for=\"price\">Price per night (USD)</label>\n");
        sb.append("<input type=\"number\" id=\"price\" name=\"price\" min=\"1\" step=\"0.01\" placeholder=\"e.g. 89.00\" required>\n");
        sb.append("<div class=\"row-actions\">\n");
        sb.append("<button type=\"submit\" class=\"btn\">Add Room</button>\n");
        sb.append("<a class=\"btn secondary\" href=\"/\">Cancel</a>\n");
        sb.append("</div>\n");
        sb.append("</form>\n");
        sb.append("</div>\n");
        return page("Add Room", sb.toString());
    }
}
