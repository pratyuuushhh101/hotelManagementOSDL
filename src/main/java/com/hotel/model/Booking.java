package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a hotel booking linking a customer to a room for a date range.
 * This is a plain data model with no UI or I/O dependencies.
 */
public class Booking {

    private String bookingId;
    private String customerId;
    private int roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private double totalAmount;
    private BookingStatus status;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Booking() {
    }

    /**
     * Creates a new Booking with a generated UUID and the specified details.
     *
     * @param customerId   the ID of the customer making the booking
     * @param roomNumber   the number of the booked room
     * @param checkInDate  the check-in date (ISO format string)
     * @param checkOutDate the check-out date (ISO format string)
     * @param totalAmount  the total billing amount
     * @param status       the current booking status
     */
    public Booking(String customerId, int roomNumber, String checkInDate,
                   String checkOutDate, double totalAmount, BookingStatus status) {
        this.bookingId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    /**
     * Returns the unique booking identifier.
     *
     * @return the booking ID (UUID string)
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * Sets the booking identifier.
     *
     * @param bookingId the booking ID to set
     */
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    /**
     * Returns the customer ID associated with this booking.
     *
     * @return the customer ID
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID for this booking.
     *
     * @param customerId the customer ID to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the room number associated with this booking.
     *
     * @return the room number
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the room number for this booking.
     *
     * @param roomNumber the room number to set
     */
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * Returns the check-in date as an ISO format string.
     *
     * @return the check-in date string
     */
    public String getCheckInDate() {
        return checkInDate;
    }

    /**
     * Sets the check-in date.
     *
     * @param checkInDate the check-in date to set (ISO format)
     */
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    /**
     * Returns the check-out date as an ISO format string.
     *
     * @return the check-out date string
     */
    public String getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Sets the check-out date.
     *
     * @param checkOutDate the check-out date to set (ISO format)
     */
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    /**
     * Returns the total amount for this booking.
     *
     * @return the total amount
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount for this booking.
     *
     * @param totalAmount the total amount to set
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Returns the booking status (ACTIVE or CHECKED_OUT).
     *
     * @return the booking status
     */
    public BookingStatus getStatus() {
        return status;
    }

    /**
     * Sets the booking status.
     *
     * @param status the status to set
     */
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /**
     * Calculates the number of days between check-in and check-out.
     *
     * @return the number of days stayed
     */
    public long getDaysStayed() {
        LocalDate in = LocalDate.parse(checkInDate);
        LocalDate out = LocalDate.parse(checkOutDate);
        return ChronoUnit.DAYS.between(in, out);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId.substring(0, 8) + " | Room " + roomNumber + " | " + status;
    }
}
