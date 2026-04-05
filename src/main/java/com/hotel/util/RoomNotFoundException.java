package com.hotel.util;

/**
 * Thrown when a referenced room cannot be found in the system.
 */
public class RoomNotFoundException extends Exception {

    /**
     * Constructs a new RoomNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public RoomNotFoundException(String message) {
        super(message);
    }
}
