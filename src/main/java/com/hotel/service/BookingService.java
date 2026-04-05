package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.storage.DataStore;
import com.hotel.util.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Booking operations.
 * Contains business logic — delegates persistence to the storage layer.
 */
public class BookingService {

    private List<Booking> bookings;
    private final RoomService roomService;
    private final CustomerService customerService;

    /**
     * Constructs the BookingService with its dependencies and loads existing
     * bookings.
     *
     * @param roomService     the room service for availability checks
     * @param customerService the customer service for customer lookups
     */
    public BookingService(RoomService roomService, CustomerService customerService) {
        this.roomService = roomService;
        this.customerService = customerService;
        this.bookings = DataStore.getBookingStorage().loadAll();
    }

    /**
     * Returns all bookings.
     *
     * @return list of all bookings
     */
    public List<Booking> getAllBookings() {
        return bookings;
    }

    /**
     * Returns only active bookings.
     *
     * @return list of active bookings
     */
    public List<Booking> getActiveBookings() {
        return bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Finds a booking by its ID.
     *
     * @param bookingId the booking ID to search for
     * @return the matching Booking
     * @throws BookingNotFoundException if no booking with the given ID exists
     */
    public Booking findById(String bookingId) throws BookingNotFoundException {
        return bookings.stream()
                .filter(b -> b.getBookingId().equals(bookingId))
                .findFirst()
                .orElseThrow(() -> new BookingNotFoundException("Booking " + bookingId + " not found."));
    }

    /**
     * Creates a new booking after validating availability and date logic.
     *
     * @param customerId   the customer ID
     * @param roomNumber   the room number to book
     * @param checkInDate  the check-in date
     * @param checkOutDate the check-out date
     * @return the created Booking
     * @throws RoomAlreadyBookedException if the room is not available
     * @throws CustomerNotFoundException  if the customer does not exist
     * @throws RoomNotFoundException      if the room does not exist
     * @throws ValidationException        if date logic is invalid
     */
    public Booking createBooking(String customerId, int roomNumber,
            LocalDate checkInDate, LocalDate checkOutDate)
            throws RoomAlreadyBookedException, CustomerNotFoundException, RoomNotFoundException, ValidationException {

        // Validate dates
        if (checkInDate == null || checkOutDate == null) {
            throw new ValidationException("Check-in and check-out dates are required.");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new ValidationException("Check-out date must be after check-in date.");
        }

        // Validate customer exists
        customerService.findById(customerId);

        // Validate room exists and is available
        Room room = roomService.findByRoomNumber(roomNumber);
        if (!room.isAvailable()) {
            throw new RoomAlreadyBookedException("Room " + roomNumber + " is already booked.");
        }

        // Calculate total amount
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalAmount = room.getPricePerDay() * days;

        // Create booking
        Booking booking = new Booking(
                customerId,
                roomNumber,
                checkInDate.toString(),
                checkOutDate.toString(),
                totalAmount,
                BookingStatus.ACTIVE);

        // Mark room as unavailable
        roomService.setAvailability(roomNumber, false);

        bookings.add(booking);
        save();

        return booking;
    }

    /**
     * Checks out a booking: marks booking as CHECKED_OUT and sets room available.
     *
     * @param bookingId the booking ID to checkout
     * @return the updated Booking
     * @throws BookingNotFoundException if the booking is not found
     * @throws RoomNotFoundException    if the room associated with the booking is
     *                                  not found
     * @throws ValidationException      if the booking is already checked out
     */
    public Booking checkout(String bookingId)
            throws BookingNotFoundException, RoomNotFoundException, ValidationException {
        Booking booking = findById(bookingId);

        if (booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new ValidationException("Booking is already checked out.");
        }

        booking.setStatus(BookingStatus.CHECKED_OUT);
        roomService.setAvailability(booking.getRoomNumber(), true);
        save();

        return booking;
    }

    /**
     * Returns the count of active bookings.
     *
     * @return active booking count
     */
    public long getActiveBookingCount() {
        return bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .count();
    }

    /**
     * Calculates total revenue from all bookings.
     *
     * @return total revenue
     */
    public double getTotalRevenue() {
        return bookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }

    /**
     * Reloads bookings from storage.
     */
    public void reload() {
        this.bookings = DataStore.getBookingStorage().loadAll();
    }

    /**
     * Persists the current list of bookings to storage.
     */
    private void save() {
        DataStore.getBookingStorage().saveAll(bookings);
    }
}
