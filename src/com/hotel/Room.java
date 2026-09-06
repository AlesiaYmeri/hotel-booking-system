package com.hotel;

public class Room {
    private final int id;
    private String number;
    private RoomType type;
    private double pricePerNight;

    public Room(int id, String number, RoomType type, double pricePerNight) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String toCsvLine() {
        return id + "|" + number + "|" + type.name() + "|" + pricePerNight;
    }

    public static Room fromCsvLine(String line) {
        String[] parts = line.split("\\|", -1);
        int id = Integer.parseInt(parts[0].trim());
        String number = parts[1].trim();
        RoomType type = RoomType.fromString(parts[2].trim());
        double price = Double.parseDouble(parts[3].trim());
        return new Room(id, number, type, price);
    }
}
