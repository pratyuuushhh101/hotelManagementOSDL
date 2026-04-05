package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.util.BookingNotFoundException;
import com.hotel.util.CustomerNotFoundException;
import com.hotel.util.RoomNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service for generating and exporting billing information.
 * Contains business logic for bill computation and formatting.
 */
public class BillingService {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final CustomerService customerService;

    /**
     * Constructs the BillingService with its dependencies.
     *
     * @param bookingService  the booking service
     * @param roomService     the room service
     * @param customerService the customer service
     */
    public BillingService(BookingService bookingService, RoomService roomService, CustomerService customerService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.customerService = customerService;
    }

    /**
     * Generates a formatted bill summary string for a booking.
     *
     * @param bookingId the booking ID
     * @return a detailed multi-line bill summary
     * @throws BookingNotFoundException  if the booking is not found
     * @throws CustomerNotFoundException if the customer is not found
     * @throws RoomNotFoundException     if the room is not found
     */
    public String generateBillSummary(String bookingId)
            throws BookingNotFoundException, CustomerNotFoundException, RoomNotFoundException {

        Booking booking = bookingService.findById(bookingId);
        Customer customer = customerService.findById(booking.getCustomerId());
        Room room = roomService.findByRoomNumber(booking.getRoomNumber());

        LocalDate checkIn = LocalDate.parse(booking.getCheckInDate());
        LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate());
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════╗\n");
        sb.append("║         HOTEL MANAGEMENT SYSTEM          ║\n");
        sb.append("║              BILL SUMMARY                ║\n");
        sb.append("╠══════════════════════════════════════════╣\n");
        sb.append(String.format("║ Booking ID:    %-25s ║\n", booking.getBookingId().substring(0, 8) + "..."));
        sb.append(String.format("║ Customer:      %-25s ║\n", truncate(customer.getName(), 25)));
        sb.append(String.format("║ Contact:       %-25s ║\n", customer.getContactNumber()));
        sb.append("╠══════════════════════════════════════════╣\n");
        sb.append(String.format("║ Room Number:   %-25d ║\n", room.getRoomNumber()));
        sb.append(String.format("║ Room Type:     %-25s ║\n", room.getRoomType().getDisplayName()));
        sb.append(String.format("║ Check-In:      %-25s ║\n", booking.getCheckInDate()));
        sb.append(String.format("║ Check-Out:     %-25s ║\n", booking.getCheckOutDate()));
        sb.append("╠══════════════════════════════════════════╣\n");
        sb.append(String.format("║ Days Stayed:   %-25d ║\n", days));
        sb.append(String.format("║ Price/Day:     ₹ %-23.2f ║\n", room.getPricePerDay()));
        sb.append(String.format("║ TOTAL AMOUNT:  ₹ %-23.2f ║\n", booking.getTotalAmount()));
        sb.append("╠══════════════════════════════════════════╣\n");
        sb.append(String.format("║ Status:        %-25s ║\n", booking.getStatus().getDisplayName()));
        sb.append("╚══════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Exports a bill summary to a text file.
     *
     * @param bookingId the booking ID
     * @param filePath  the filesystem path to write the bill to
     * @throws BookingNotFoundException  if the booking is not found
     * @throws CustomerNotFoundException if the customer is not found
     * @throws RoomNotFoundException     if the room is not found
     * @throws IOException               if an I/O error occurs during file writing
     */
    public void exportBillToFile(String bookingId, String filePath)
            throws BookingNotFoundException, CustomerNotFoundException, RoomNotFoundException, IOException {

        String summary = generateBillSummary(bookingId);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(summary);
        }
    }

    /**
     * Truncates a string to the specified max length, appending "..." if truncated.
     *
     * @param text      the text to truncate
     * @param maxLength the maximum length
     * @return the truncated string
     */
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
