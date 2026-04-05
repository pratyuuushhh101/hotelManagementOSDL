package com.hotel.model;

/**
 * Enumeration representing the status of a hotel booking.
 */
public enum BookingStatus {
    /** Booking is currently active — guest is checked in */
    ACTIVE("Active"),
    /** Booking is completed — guest has checked out */
    CHECKED_OUT("Checked Out");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this status.
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
