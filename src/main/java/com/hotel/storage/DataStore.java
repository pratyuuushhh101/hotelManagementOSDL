package com.hotel.storage;

import com.google.gson.reflect.TypeToken;
import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Factory and accessor for all JSON-based storage instances.
 * Provides typed storage objects for rooms, customers, and bookings.
 */
public final class DataStore {

    private static final Type ROOM_LIST_TYPE = new TypeToken<List<Room>>() {}.getType();
    private static final Type CUSTOMER_LIST_TYPE = new TypeToken<List<Customer>>() {}.getType();
    private static final Type BOOKING_LIST_TYPE = new TypeToken<List<Booking>>() {}.getType();

    private static final JsonStorage<Room> roomStorage = new JsonStorage<>("rooms.json", ROOM_LIST_TYPE);
    private static final JsonStorage<Customer> customerStorage = new JsonStorage<>("customers.json", CUSTOMER_LIST_TYPE);
    private static final JsonStorage<Booking> bookingStorage = new JsonStorage<>("bookings.json", BOOKING_LIST_TYPE);

    private DataStore() {
        // Utility class — no instantiation
    }

    /**
     * Returns the storage handler for Room objects.
     *
     * @return the room JsonStorage
     */
    public static JsonStorage<Room> getRoomStorage() {
        return roomStorage;
    }

    /**
     * Returns the storage handler for Customer objects.
     *
     * @return the customer JsonStorage
     */
    public static JsonStorage<Customer> getCustomerStorage() {
        return customerStorage;
    }

    /**
     * Returns the storage handler for Booking objects.
     *
     * @return the booking JsonStorage
     */
    public static JsonStorage<Booking> getBookingStorage() {
        return bookingStorage;
    }
}
