package com.hotel.util;

/**
 * Thrown when a referenced customer cannot be found in the system.
 */
public class CustomerNotFoundException extends Exception {

    /**
     * Constructs a new CustomerNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
