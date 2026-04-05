package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.service.BillingService;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.util.BookingNotFoundException;
import com.hotel.util.CustomerNotFoundException;
import com.hotel.util.RoomNotFoundException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;

/**
 * Controller for the Billing tab (billing.fxml).
 * Manages bill generation, preview, and export.
 */
public class BillingController {

    @FXML
    private ComboBox<Booking> bookingCombo;
    @FXML
    private Button generateBillBtn;
    @FXML
    private Button exportBillBtn;
    @FXML
    private TextArea billPreview;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Booking> billingTable;
    @FXML
    private TableColumn<Booking, String> colBookingId;
    @FXML
    private TableColumn<Booking, String> colCustomerName;
    @FXML
    private TableColumn<Booking, String> colRoomNumber;
    @FXML
    private TableColumn<Booking, String> colDays;
    @FXML
    private TableColumn<Booking, String> colTotal;
    @FXML
    private TableColumn<Booking, String> colStatus;

    private BillingService billingService;
    private BookingService bookingService;
    private CustomerService customerService;
    private RoomService roomService;
    private ObservableList<Booking> bookingList;
    private FilteredList<Booking> filteredBookings;
    private String currentBillBookingId = null;

    /**
     * Called after FXML injection. Sets up combo, table, and listeners.
     */
    @FXML
    public void initialize() {
        bookingService = ServiceLocator.getBookingService();
        customerService = ServiceLocator.getCustomerService();
        roomService = ServiceLocator.getRoomService();
        billingService = ServiceLocator.getBillingService();

        setupCombo();
        setupTable();
        loadBookings();

        // Search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // Table selection -> select in combo
        billingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                bookingCombo.setValue(newVal);
            }
        });

        // Export disabled until a bill is generated
        exportBillBtn.setDisable(true);
    }

    /**
     * Configures the booking ComboBox.
     */
    private void setupCombo() {
        bookingCombo.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
        bookingCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Booking b) {
                if (b == null)
                    return "";
                String customerName;
                try {
                    customerName = customerService.findById(b.getCustomerId()).getName();
                } catch (CustomerNotFoundException e) {
                    customerName = "Unknown";
                }
                return b.getBookingId().substring(0, 8) + "... | "
                        + customerName + " | Room " + b.getRoomNumber()
                        + " | " + b.getStatus().getDisplayName();
            }

            @Override
            public Booking fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Configures the billing table columns.
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
        colDays.setCellValueFactory(data -> {
            try {
                return new SimpleStringProperty(String.valueOf(data.getValue().getDaysStayed()));
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });
        colTotal.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("₹%.2f", data.getValue().getTotalAmount())));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
    }

    /**
     * Loads bookings into the table.
     */
    private void loadBookings() {
        bookingList = FXCollections.observableArrayList(bookingService.getAllBookings());
        filteredBookings = new FilteredList<>(bookingList, p -> true);
        billingTable.setItems(filteredBookings);
    }

    /**
     * Applies the search filter.
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
                    || String.valueOf(booking.getRoomNumber()).contains(query);
        });
    }

    /**
     * Public refresh method called by MainController when the Billing tab is
     * selected.
     * Reloads the booking combo and table to reflect latest data.
     */
    public void refresh() {
        bookingCombo.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));
        bookingList.setAll(bookingService.getAllBookings());
        applyFilter();
    }

    /**
     * Handles the Generate Bill button click.
     */
    @FXML
    private void handleGenerateBill() {
        Booking selected = bookingCombo.getValue();
        if (selected == null) {
            showError("No Selection", "Please select a booking to generate a bill.");
            return;
        }

        try {
            String bill = billingService.generateBillSummary(selected.getBookingId());
            billPreview.setText(bill);
            currentBillBookingId = selected.getBookingId();
            exportBillBtn.setDisable(false);
        } catch (BookingNotFoundException | CustomerNotFoundException | RoomNotFoundException e) {
            showError("Bill Generation Failed", e.getMessage());
        }
    }

    /**
     * Handles the Export as TXT button click.
     */
    @FXML
    private void handleExportBill() {
        if (currentBillBookingId == null) {
            showError("No Bill", "Please generate a bill first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Bill");
        fileChooser.setInitialFileName("bill_" + currentBillBookingId.substring(0, 8) + ".txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(billingTable.getScene().getWindow());
        if (file != null) {
            try {
                billingService.exportBillToFile(currentBillBookingId, file.getAbsolutePath());
                showSuccess("Bill exported to:\n" + file.getAbsolutePath());
            } catch (BookingNotFoundException | CustomerNotFoundException
                    | RoomNotFoundException | IOException e) {
                showError("Export Failed", e.getMessage());
            }
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
}
