package com.hotel.controller;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.RoomService;
import com.hotel.util.RoomNotFoundException;
import com.hotel.util.ValidationException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * Controller for the Rooms tab (rooms.fxml).
 * Manages room CRUD, filtering, and table display.
 */
public class RoomController {

    @FXML
    private TextField roomNumberField;
    @FXML
    private ComboBox<RoomType> roomTypeCombo;
    @FXML
    private TextField priceField;
    @FXML
    private Label roomNumberError;
    @FXML
    private Label roomTypeError;
    @FXML
    private Label priceError;
    @FXML
    private Button addRoomBtn;
    @FXML
    private Button updateRoomBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button deleteRoomBtn;
    @FXML
    private TextField searchField;
    @FXML
    private ToggleButton filterAvailableBtn;
    @FXML
    private TableView<Room> roomsTable;
    @FXML
    private TableColumn<Room, Integer> colRoomNumber;
    @FXML
    private TableColumn<Room, String> colRoomType;
    @FXML
    private TableColumn<Room, Double> colPrice;
    @FXML
    private TableColumn<Room, String> colAvailable;

    private RoomService roomService;
    private ObservableList<Room> roomList;
    private FilteredList<Room> filteredRooms;

    /**
     * Called after FXML injection. Sets up the form, table, context menu, and
     * listeners.
     */
    @FXML
    public void initialize() {
        roomService = ServiceLocator.getRoomService();

        // Setup ComboBox
        roomTypeCombo.setItems(FXCollections.observableArrayList(RoomType.values()));

        // Setup table columns
        colRoomNumber
                .setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getRoomNumber()).asObject());
        colRoomType
                .setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoomType().getDisplayName()));
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPricePerDay()).asObject());
        colAvailable.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().isAvailable() ? "✅ Yes" : "❌ No"));

        // Load data
        loadRooms();

        // Search filtering
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Table selection -> fill form
        roomsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });

        // Context menu
        setupContextMenu();
    }

    /**
     * Loads rooms into the table with a filtered wrapper.
     */
    private void loadRooms() {
        roomList = FXCollections.observableArrayList(roomService.getAllRooms());
        filteredRooms = new FilteredList<>(roomList, p -> true);
        roomsTable.setItems(filteredRooms);
    }

    /**
     * Applies search and availability filters to the table.
     */
    private void applyFilters() {
        String query = searchField.getText().toLowerCase().trim();
        boolean showAvailableOnly = filterAvailableBtn.isSelected();

        filteredRooms.setPredicate(room -> {
            boolean matchesSearch = query.isEmpty()
                    || String.valueOf(room.getRoomNumber()).contains(query)
                    || room.getRoomType().getDisplayName().toLowerCase().contains(query);
            boolean matchesAvailable = !showAvailableOnly || room.isAvailable();
            return matchesSearch && matchesAvailable;
        });
    }

    /**
     * Fills the form fields with data from the selected room.
     *
     * @param room the selected room
     */
    private void fillForm(Room room) {
        roomNumberField.setText(String.valueOf(room.getRoomNumber()));
        roomNumberField.setDisable(true); // Can't change room number on edit
        roomTypeCombo.setValue(room.getRoomType());
        priceField.setText(String.valueOf(room.getPricePerDay()));
        clearErrors();
    }

    /**
     * Sets up the right-click context menu on table rows.
     */
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("✏ Edit");
        editItem.setOnAction(e -> {
            Room selected = roomsTable.getSelectionModel().getSelectedItem();
            if (selected != null)
                fillForm(selected);
        });

        MenuItem deleteItem = new MenuItem("🗑 Delete");
        deleteItem.setOnAction(e -> handleDeleteRoom());

        MenuItem viewItem = new MenuItem("👁 View Details");
        viewItem.setOnAction(e -> {
            Room selected = roomsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showInfo("Room Details",
                        "Room #" + selected.getRoomNumber() + "\n"
                                + "Type: " + selected.getRoomType().getDisplayName() + "\n"
                                + "Price/Day: ₹" + String.format("%.2f", selected.getPricePerDay()) + "\n"
                                + "Available: " + (selected.isAvailable() ? "Yes" : "No"));
            }
        });

        contextMenu.getItems().addAll(editItem, deleteItem, viewItem);
        roomsTable.setContextMenu(contextMenu);
    }

    /**
     * Handles the Add Room button click.
     */
    @FXML
    private void handleAddRoom() {
        clearErrors();
        try {
            int roomNumber = parseRoomNumber();
            RoomType type = roomTypeCombo.getValue();
            double price = parsePrice();

            roomService.addRoom(roomNumber, type, price);
            showSuccess("Room " + roomNumber + " added successfully!");
            handleClear();
            refreshTable();
        } catch (ValidationException e) {
            showValidationError(e.getMessage());
        }
    }

    /**
     * Handles the Update Room button click.
     */
    @FXML
    private void handleUpdateRoom() {
        clearErrors();
        try {
            int roomNumber = parseRoomNumber();
            RoomType type = roomTypeCombo.getValue();
            double price = parsePrice();

            roomService.updateRoom(roomNumber, type, price);
            showSuccess("Room " + roomNumber + " updated successfully!");
            handleClear();
            refreshTable();
        } catch (ValidationException | RoomNotFoundException e) {
            showError("Update Failed", e.getMessage());
        }
    }

    /**
     * Handles the Delete Room button click.
     */
    @FXML
    private void handleDeleteRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a room to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Delete Room", "Are you sure you want to delete Room " + selected.getRoomNumber() + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                roomService.deleteRoom(selected.getRoomNumber());
                showSuccess("Room " + selected.getRoomNumber() + " deleted.");
                handleClear();
                refreshTable();
            } catch (RoomNotFoundException e) {
                showError("Delete Failed", e.getMessage());
            }
        }
    }

    /**
     * Handles the availability filter toggle.
     */
    @FXML
    private void handleFilterToggle() {
        applyFilters();
    }

    /**
     * Clears all form fields and errors.
     */
    @FXML
    private void handleClear() {
        roomNumberField.setText("");
        roomNumberField.setDisable(false);
        roomTypeCombo.setValue(null);
        priceField.setText("");
        clearErrors();
        roomsTable.getSelectionModel().clearSelection();
    }

    /**
     * Refreshes the table from the service data.
     */
    private void refreshTable() {
        roomList.setAll(roomService.getAllRooms());
        applyFilters();
    }

    /**
     * Parses the room number from the text field.
     *
     * @return the parsed room number
     * @throws ValidationException if the input is not a valid positive integer
     */
    private int parseRoomNumber() throws ValidationException {
        String text = roomNumberField.getText().trim();
        if (text.isEmpty()) {
            roomNumberError.setText("Room number is required");
            throw new ValidationException("Room number is required.");
        }
        try {
            int num = Integer.parseInt(text);
            if (num <= 0) {
                roomNumberError.setText("Must be positive");
                throw new ValidationException("Room number must be a positive integer.");
            }
            return num;
        } catch (NumberFormatException e) {
            roomNumberError.setText("Must be a number");
            throw new ValidationException("Room number must be a valid integer.");
        }
    }

    /**
     * Parses the price from the text field.
     *
     * @return the parsed price value
     * @throws ValidationException if the input is not a valid positive number
     */
    private double parsePrice() throws ValidationException {
        String text = priceField.getText().trim();
        if (text.isEmpty()) {
            priceError.setText("Price is required");
            throw new ValidationException("Price per day is required.");
        }
        try {
            double price = Double.parseDouble(text);
            if (price <= 0) {
                priceError.setText("Must be positive");
                throw new ValidationException("Price per day must be positive.");
            }
            return price;
        } catch (NumberFormatException e) {
            priceError.setText("Must be a number");
            throw new ValidationException("Price must be a valid number.");
        }
    }

    /**
     * Clears all inline error labels.
     */
    private void clearErrors() {
        roomNumberError.setText("");
        roomTypeError.setText("");
        priceError.setText("");
    }

    /**
     * Shows a validation error in the appropriate inline label.
     *
     * @param message the error message
     */
    private void showValidationError(String message) {
        if (message.toLowerCase().contains("room number")) {
            roomNumberError.setText(message);
        } else if (message.toLowerCase().contains("room type") || message.toLowerCase().contains("selected")) {
            roomTypeError.setText(message);
        } else if (message.toLowerCase().contains("price")) {
            priceError.setText(message);
        } else {
            showError("Validation Error", message);
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
