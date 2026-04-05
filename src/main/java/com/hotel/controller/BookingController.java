package com.hotel.controller;

import com.hotel.model.*;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Bookings tab (bookings.fxml).
 * Manages booking creation, checkout, and table display.
 */
public class BookingController {

    @FXML
    private ComboBox<Customer> customerCombo;
    @FXML
    private ComboBox<Room> roomCombo;
    @FXML
    private DatePicker checkInPicker;
    @FXML
    private DatePicker checkOutPicker;
    @FXML
    private Label customerError;
    @FXML
    private Label roomError;
    @FXML
    private Label checkInError;
    @FXML
    private Label checkOutError;
    @FXML
    private Button bookRoomBtn;
    @FXML
    private Button checkoutBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Booking> bookingsTable;
    @FXML
    private TableColumn<Booking, String> colBookingId;
    @FXML
    private TableColumn<Booking, String> colCustomerName;
    @FXML
    private TableColumn<Booking, String> colRoomNumber;
    @FXML
    private TableColumn<Booking, String> colCheckIn;
    @FXML
    private TableColumn<Booking, String> colCheckOut;
    @FXML
    private TableColumn<Booking, String> colTotal;
    @FXML
    private TableColumn<Booking, String> colStatus;

    private BookingService bookingService;
    private RoomService roomService;
    private CustomerService customerService;
    private ObservableList<Booking> bookingList;
    private FilteredList<Booking> filteredBookings;

    /**
     * Called after FXML injection. Sets up combos, table, and listeners.
     */
    @FXML
    public void initialize() {
        bookingService = ServiceLocator.getBookingService();
        roomService = ServiceLocator.getRoomService();
        customerService = ServiceLocator.getCustomerService();

        setupCombos();
        setupTable();
        loadBookings();
        setupContextMenu();

        // Search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // Disable book button if no available rooms
        updateBookButtonState();
    }

    /**
     * Configures the customer and room ComboBoxes.
     */
    private void setupCombos() {
        // Customer combo
        customerCombo.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        customerCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer c) {
                return c == null ? "" : c.getName() + " (" + c.getContactNumber() + ")";
            }

            @Override
            public Customer fromString(String s) {
                return null;
            }
        });

        // Room combo — only available rooms
        refreshRoomCombo();
        roomCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room r) {
                return r == null ? ""
                        : "Room " + r.getRoomNumber() + " - " + r.getRoomType()
                                + " (₹" + String.format("%.0f", r.getPricePerDay()) + "/day)";
            }

            @Override
            public Room fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Refreshes the room combo to show only available rooms.
     */
    private void refreshRoomCombo() {
        List<Room> available = roomService.getAvailableRooms();
        roomCombo.setItems(FXCollections.observableArrayList(available));
        updateBookButtonState();
    }

    /**
     * Refreshes the customer combo with latest data.
     */
    private void refreshCustomerCombo() {
        customerCombo.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
    }

    /**
     * Configures the bookings table columns.
     */
    private void setupTable() {
        colBookingId.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getBookingId().substring(0, 8) + "..."));
        colCustomerName.setCellValueFactory(data -> {
            try {
                Customer c = customerService.findById(data.getValue().getCustomerId());
                return new SimpleStringProperty(c.getName());
            } catch (CustomerNotFoundException e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colRoomNumber
                .setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRoomNumber())));
        colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate()));
        colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate()));
        colTotal.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("₹%.2f", data.getValue().getTotalAmount())));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
    }

    /**
     * Loads bookings into the table with a filtered wrapper.
     */
    private void loadBookings() {
        bookingList = FXCollections.observableArrayList(bookingService.getAllBookings());
        filteredBookings = new FilteredList<>(bookingList, p -> true);
        bookingsTable.setItems(filteredBookings);
    }

    /**
     * Applies the search filter to bookings.
     */
    private void applyFilter() {
        String query = searchField.getText().toLowerCase().trim();
        filteredBookings.setPredicate(booking -> {
            if (query.isEmpty())
                return true;
            String customerName;
            try {
                customerName = customerService.findById(booking.getCustomerId()).getName().toLowerCase();
            } catch (CustomerNotFoundException e) {
                customerName = "";
            }
            return booking.getBookingId().toLowerCase().contains(query)
                    || customerName.contains(query)
                    || String.valueOf(booking.getRoomNumber()).contains(query)
                    || booking.getStatus().getDisplayName().toLowerCase().contains(query);
        });
    }

    /**
     * Sets up the right-click context menu.
     */
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem checkoutItem = new MenuItem("✅ Checkout");
        checkoutItem.setOnAction(e -> handleCheckout());

        MenuItem viewItem = new MenuItem("👁 View Details");
        viewItem.setOnAction(e -> {
            Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String customerName;
                try {
                    customerName = customerService.findById(selected.getCustomerId()).getName();
                } catch (CustomerNotFoundException ex) {
                    customerName = "Unknown";
                }
                showInfo("Booking Details",
                        "Booking ID: " + selected.getBookingId() + "\n"
                                + "Customer: " + customerName + "\n"
                                + "Room: " + selected.getRoomNumber() + "\n"
                                + "Check-In: " + selected.getCheckInDate() + "\n"
                                + "Check-Out: " + selected.getCheckOutDate() + "\n"
                                + "Total: ₹" + String.format("%.2f", selected.getTotalAmount()) + "\n"
                                + "Status: " + selected.getStatus().getDisplayName());
            }
        });

        contextMenu.getItems().addAll(checkoutItem, viewItem);
        bookingsTable.setContextMenu(contextMenu);
    }

    /**
     * Handles the Book Room button click.
     */
    @FXML
    private void handleBookRoom() {
        clearErrors();
        boolean hasError = false;

        Customer customer = customerCombo.getValue();
        Room room = roomCombo.getValue();
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        if (customer == null) {
            customerError.setText("Please select a customer");
            hasError = true;
        }
        if (room == null) {
            roomError.setText("Please select a room");
            hasError = true;
        }
        if (checkIn == null) {
            checkInError.setText("Check-in date is required");
            hasError = true;
        }
        if (checkOut == null) {
            checkOutError.setText("Check-out date is required");
            hasError = true;
        }
        if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) {
            checkOutError.setText("Must be after check-in date");
            hasError = true;
        }

        if (hasError)
            return;

        try {
            Booking booking = bookingService.createBooking(
                    customer.getCustomerId(),
                    room.getRoomNumber(),
                    checkIn,
                    checkOut);
            showSuccess("Booking created successfully!\nTotal Amount: ₹"
                    + String.format("%.2f", booking.getTotalAmount()));
            handleClear();
            refreshAll();
        } catch (RoomAlreadyBookedException | CustomerNotFoundException
                | RoomNotFoundException | ValidationException e) {
            showError("Booking Failed", e.getMessage());
        }
    }

    /**
     * Handles the Checkout button click.
     */
    @FXML
    private void handleCheckout() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a booking to check out.");
            return;
        }
        if (selected.getStatus() == BookingStatus.CHECKED_OUT) {
            showError("Already Checked Out", "This booking is already checked out.");
            return;
        }

        Optional<ButtonType> result = showConfirm("Checkout",
                "Check out booking for Room " + selected.getRoomNumber() + "?\n"
                        + "Total Amount: ₹" + String.format("%.2f", selected.getTotalAmount()));

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Booking updated = bookingService.checkout(selected.getBookingId());

                // Show bill summary
                String customerName;
                try {
                    customerName = customerService.findById(updated.getCustomerId()).getName();
                } catch (CustomerNotFoundException ex) {
                    customerName = "Unknown";
                }

                Room room;
                try {
                    room = roomService.findByRoomNumber(updated.getRoomNumber());
                } catch (RoomNotFoundException ex) {
                    room = null;
                }

                long days = updated.getDaysStayed();
                String billSummary = "═══ BILL SUMMARY ═══\n\n"
                        + "Customer: " + customerName + "\n"
                        + "Room #: " + updated.getRoomNumber()
                        + (room != null ? " (" + room.getRoomType() + ")" : "") + "\n"
                        + "Check-In: " + updated.getCheckInDate() + "\n"
                        + "Check-Out: " + updated.getCheckOutDate() + "\n"
                        + "Days Stayed: " + days + "\n"
                        + (room != null ? "Price/Day: ₹" + String.format("%.2f", room.getPricePerDay()) + "\n" : "")
                        + "\n💰 TOTAL AMOUNT: ₹" + String.format("%.2f", updated.getTotalAmount());

                Alert billAlert = new Alert(Alert.AlertType.INFORMATION);
                billAlert.setTitle("Checkout Complete — Bill Summary");
                billAlert.setHeaderText("Guest checked out successfully!");
                billAlert.setContentText(billSummary);
                billAlert.showAndWait();

                refreshAll();
            } catch (BookingNotFoundException | RoomNotFoundException | ValidationException e) {
                showError("Checkout Failed", e.getMessage());
            }
        }
    }

    /**
     * Clears the booking form.
     */
    @FXML
    private void handleClear() {
        customerCombo.setValue(null);
        roomCombo.setValue(null);
        checkInPicker.setValue(null);
        checkOutPicker.setValue(null);
        clearErrors();
        bookingsTable.getSelectionModel().clearSelection();
    }

    /**
     * Public refresh method called by MainController when the Bookings tab is
     * selected.
     * Reloads all combos and table data to reflect changes made in other tabs.
     */
    public void refresh() {
        refreshAll();
    }

    /**
     * Refreshes all data (combos, table, button state).
     */
    private void refreshAll() {
        refreshRoomCombo();
        refreshCustomerCombo();
        bookingList.setAll(bookingService.getAllBookings());
        applyFilter();
    }

    /**
     * Disables the Book Room button if no available rooms.
     */
    private void updateBookButtonState() {
        bookRoomBtn.setDisable(roomService.getAvailableRoomCount() == 0);
    }

    /**
     * Clears all inline error labels.
     */
    private void clearErrors() {
        customerError.setText("");
        roomError.setText("");
        checkInError.setText("");
        checkOutError.setText("");
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
