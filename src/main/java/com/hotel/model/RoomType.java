package com.hotel.model;

/**
 * Enumeration representing the types of rooms available in the hotel.
 * Each room type has a display name for UI presentation.
 */
public enum RoomType {
    /** Single occupancy room */
    SINGLE("Single"),
    /** Double occupancy room */
    DOUBLE("Double"),
    /** Deluxe room with premium amenities */
    DELUXE("Deluxe");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this room type.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
