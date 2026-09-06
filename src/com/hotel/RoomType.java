package com.hotel;

public enum RoomType {
    SINGLE("Single"),
    DOUBLE("Double"),
    SUITE("Suite"),
    DELUXE("Deluxe");

    private final String label;

    RoomType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static RoomType fromString(String s) {
        for (RoomType t : values()) {
            if (t.name().equalsIgnoreCase(s)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown room type: " + s);
    }
}
