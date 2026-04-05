package com.hotel.model;

import java.util.Objects;

/**
 * Represents a hotel room with its properties.
 * This is a plain data model with no UI or I/O dependencies.
 */
public class Room {

    private int roomNumber;
    private RoomType roomType;
    private double pricePerDay;
    private boolean available;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Room() {
    }

    /**
     * Creates a new Room with the specified properties.
     *
     * @param roomNumber  the unique room number
     * @param roomType    the type of room (SINGLE, DOUBLE, DELUXE)
     * @param pricePerDay the daily rate for the room
     * @param available   whether the room is currently available for booking
     */
    public Room(int roomNumber, RoomType roomType, double pricePerDay, boolean available) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    /**
     * Returns the room number.
     *
     * @return the room number
     */
    public int getRoomNumber() {
        return roomNumber;
    }

    /**
     * Sets the room number.
     *
     * @param roomNumber the room number to set
     */
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * Returns the room type.
     *
     * @return the room type
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * Sets the room type.
     *
     * @param roomType the room type to set
     */
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    /**
     * Returns the price per day.
     *
     * @return the daily rate
     */
    public double getPricePerDay() {
        return pricePerDay;
    }

    /**
     * Sets the price per day.
     *
     * @param pricePerDay the daily rate to set
     */
    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    /**
     * Returns whether the room is available.
     *
     * @return true if the room is available for booking
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability status of the room.
     *
     * @param available the availability status to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomNumber == room.roomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + roomType + ") - ₹" + String.format("%.2f", pricePerDay) + "/day";
    }
}
