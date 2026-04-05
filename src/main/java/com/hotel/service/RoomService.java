package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.storage.DataStore;
import com.hotel.util.RoomNotFoundException;
import com.hotel.util.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Room operations.
 * Contains business logic — delegates persistence to the storage layer.
 */
public class RoomService {

    private List<Room> rooms;

    /**
     * Constructs the RoomService and loads existing rooms from storage.
     */
    public RoomService() {
        this.rooms = DataStore.getRoomStorage().loadAll();
    }

    /**
     * Returns all rooms.
     *
     * @return list of all rooms
     */
    public List<Room> getAllRooms() {
        return rooms;
    }

    /**
     * Returns only the rooms currently available for booking.
     *
     * @return list of available rooms
     */
    public List<Room> getAvailableRooms() {
        return rooms.stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
    }

    /**
     * Finds a room by its room number.
     *
     * @param roomNumber the room number to search for
     * @return the matching Room
     * @throws RoomNotFoundException if no room with the given number exists
     */
    public Room findByRoomNumber(int roomNumber) throws RoomNotFoundException {
        return rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .orElseThrow(() -> new RoomNotFoundException("Room " + roomNumber + " not found."));
    }

    /**
     * Adds a new room after validating the input.
     *
     * @param roomNumber  the room number
     * @param roomType    the type of room
     * @param pricePerDay the daily rate
     * @throws ValidationException if input is invalid or room number already exists
     */
    public void addRoom(int roomNumber, RoomType roomType, double pricePerDay) throws ValidationException {
        if (roomNumber <= 0) {
            throw new ValidationException("Room number must be a positive integer.");
        }
        if (roomType == null) {
            throw new ValidationException("Room type must be selected.");
        }
        if (pricePerDay <= 0) {
            throw new ValidationException("Price per day must be positive.");
        }
        boolean exists = rooms.stream().anyMatch(r -> r.getRoomNumber() == roomNumber);
        if (exists) {
            throw new ValidationException("Room " + roomNumber + " already exists.");
        }

        Room room = new Room(roomNumber, roomType, pricePerDay, true);
        rooms.add(room);
        save();
    }

    /**
     * Updates an existing room's details.
     *
     * @param roomNumber     the room number to update
     * @param newRoomType    the new room type
     * @param newPricePerDay the new daily rate
     * @throws RoomNotFoundException if the room is not found
     * @throws ValidationException   if input is invalid
     */
    public void updateRoom(int roomNumber, RoomType newRoomType, double newPricePerDay)
            throws RoomNotFoundException, ValidationException {
        if (newRoomType == null) {
            throw new ValidationException("Room type must be selected.");
        }
        if (newPricePerDay <= 0) {
            throw new ValidationException("Price per day must be positive.");
        }
        Room room = findByRoomNumber(roomNumber);
        room.setRoomType(newRoomType);
        room.setPricePerDay(newPricePerDay);
        save();
    }

    /**
     * Deletes a room by its room number.
     *
     * @param roomNumber the room number to delete
     * @throws RoomNotFoundException if the room is not found
     */
    public void deleteRoom(int roomNumber) throws RoomNotFoundException {
        Room room = findByRoomNumber(roomNumber);
        rooms.remove(room);
        save();
    }

    /**
     * Sets the availability flag for a given room.
     *
     * @param roomNumber the room number
     * @param available  the new availability status
     * @throws RoomNotFoundException if the room is not found
     */
    public void setAvailability(int roomNumber, boolean available) throws RoomNotFoundException {
        Room room = findByRoomNumber(roomNumber);
        room.setAvailable(available);
        save();
    }

    /**
     * Returns the total number of rooms.
     *
     * @return total room count
     */
    public long getTotalRooms() {
        return rooms.size();
    }

    /**
     * Returns the count of available rooms.
     *
     * @return available room count
     */
    public long getAvailableRoomCount() {
        return rooms.stream().filter(Room::isAvailable).count();
    }

    /**
     * Reloads rooms from storage. Useful after external changes.
     */
    public void reload() {
        this.rooms = DataStore.getRoomStorage().loadAll();
    }

    /**
     * Persists the current list of rooms to storage.
     */
    private void save() {
        DataStore.getRoomStorage().saveAll(rooms);
    }
}
