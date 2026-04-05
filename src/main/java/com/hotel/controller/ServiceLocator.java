package com.hotel.controller;

import com.hotel.service.BillingService;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;

/**
 * Simple service locator providing singleton instances of all application
 * services.
 * Initialized once at application startup and shared across all controllers.
 */
public final class ServiceLocator {

    private static RoomService roomService;
    private static CustomerService customerService;
    private static BookingService bookingService;
    private static BillingService billingService;

    private ServiceLocator() {
        // Utility class — no instantiation
    }

    /**
     * Initializes all services. Must be called once before any controller is
     * loaded.
     */
    public static void init() {
        roomService = new RoomService();
        customerService = new CustomerService();
        bookingService = new BookingService(roomService, customerService);
        billingService = new BillingService(bookingService, roomService, customerService);
    }

    /**
     * Returns the RoomService singleton.
     *
     * @return the room service
     */
    public static RoomService getRoomService() {
        return roomService;
    }

    /**
     * Returns the CustomerService singleton.
     *
     * @return the customer service
     */
    public static CustomerService getCustomerService() {
        return customerService;
    }

    /**
     * Returns the BookingService singleton.
     *
     * @return the booking service
     */
    public static BookingService getBookingService() {
        return bookingService;
    }

    /**
     * Returns the BillingService singleton.
     *
     * @return the billing service
     */
    public static BillingService getBillingService() {
        return billingService;
    }
}
