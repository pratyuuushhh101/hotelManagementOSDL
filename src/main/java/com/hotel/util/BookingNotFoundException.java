package com.hotel.util;

/**
 * Thrown when a referenced booking cannot be found in the system.
 */
public class BookingNotFoundException extends Exception {

    /**
     * Constructs a new BookingNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public BookingNotFoundException(String message) {
        super(message);
    }
}
