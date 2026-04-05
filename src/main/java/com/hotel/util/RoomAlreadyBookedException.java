package com.hotel.util;

/**
 * Thrown when an attempt is made to book a room that is already occupied.
 */
public class RoomAlreadyBookedException extends Exception {

    /**
     * Constructs a new RoomAlreadyBookedException with the specified message.
     *
     * @param message the detail message
     */
    public RoomAlreadyBookedException(String message) {
        super(message);
    }
}
