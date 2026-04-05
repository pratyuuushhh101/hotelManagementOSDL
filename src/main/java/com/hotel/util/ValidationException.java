package com.hotel.util;

/**
 * Thrown when input validation fails.
 */
public class ValidationException extends Exception {

    /**
     * Constructs a new ValidationException with the specified message.
     *
     * @param message the detail message describing the validation error
     */
    public ValidationException(String message) {
        super(message);
    }
}
