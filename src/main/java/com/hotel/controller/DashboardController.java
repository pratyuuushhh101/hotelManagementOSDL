package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.BookingStatus;
import com.hotel.model.Customer;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.util.CustomerNotFoundException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller for the Dashboard tab (dashboard.fxml).
 * Displays summary statistics and recent bookings.
 */
public class DashboardController {

    @FXML
    private HBox cardsBox;
    @FXML
    private TableView<Booking> recentBookingsTable;
    @FXML
    private TableColumn<Booking, String> colBookingId;
    @FXML
    private TableColumn<Booking, String> colCustomer;
    @FXML
    private TableColumn<Booking, String> colRoom;
    @FXML
    private TableColumn<Booking, String> colCheckIn;
    @FXML
    private TableColumn<Booking, String> colCheckOut;
    @FXML
    private TableColumn<Booking, String> colStatus;

    private RoomService roomService;
    private CustomerService customerService;
    private BookingService bookingService;

    /**
     * Called after FXML injection. Sets up services, cards, and table.
     */
    @FXML
    public void initialize() {
        roomService = ServiceLocator.getRoomService();
        customerService = ServiceLocator.getCustomerService();
        bookingService = ServiceLocator.getBookingService();

        setupCards();
        setupTable();
        loadRecentBookings();
    }

    /**
     * Creates and displays the summary dashboard cards.
     */
    private void setupCards() {
        cardsBox.getChildren().clear();

        long totalRooms = roomService.getTotalRooms();
        long availableRooms = roomService.getAvailableRoomCount();
        long activeBookings = bookingService.getActiveBookingCount();
        double totalRevenue = bookingService.getTotalRevenue();

        cardsBox.getChildren().addAll(
                createCard("🏨", String.valueOf(totalRooms), "Total Rooms"),
                createCard("✅", String.valueOf(availableRooms), "Available Rooms"),
                createCard("📋", String.valueOf(activeBookings), "Active Bookings"),
                createCard("💰", String.format("₹%.0f", totalRevenue), "Total Revenue"));
    }

    /**
     * Builds a single dashboard summary card.
     *
     * @param icon  the Unicode icon to display
     * @param value the numeric value text
     * @param title the card description
     * @return a styled VBox card
     */
    private VBox createCard(String icon, String value, String title) {
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("card-icon");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("card-value");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        VBox card = new VBox(8, iconLabel, valueLabel, titleLabel);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("dashboard-card");
        return card;
    }

    /**
     * Configures the recent bookings table columns.
     */
    private void setupTable() {
        colBookingId.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getBookingId().substring(0, 8) + "..."));
        colCustomer.setCellValueFactory(data -> {
            try {
                Customer c = customerService.findById(data.getValue().getCustomerId());
                return new SimpleStringProperty(c.getName());
            } catch (CustomerNotFoundException e) {
                return new SimpleStringProperty("Unknown");
            }
        });
        colRoom.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRoomNumber())));
        colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate()));
        colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
    }

    /**
     * Loads the most recent bookings (up to 10) into the table.
     */
    private void loadRecentBookings() {
        List<Booking> allBookings = bookingService.getAllBookings();
        int size = allBookings.size();
        List<Booking> recent = allBookings.subList(Math.max(0, size - 10), size);
        ObservableList<Booking> data = FXCollections.observableArrayList(recent);
        recentBookingsTable.setItems(data);
    }

    /**
     * Refreshes all dashboard data. Called when the tab is selected.
     */
    public void refresh() {
        setupCards();
        loadRecentBookings();
    }
}
