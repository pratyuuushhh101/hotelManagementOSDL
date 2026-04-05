package com.hotel.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;

/**
 * Controller for the main application layout (main.fxml).
 * Manages the top-level TabPane navigation and refreshes
 * child controllers when their respective tabs are selected.
 */
public class MainController {

    @FXML
    private TabPane tabPane;

    /*
     * Injected sub-controllers via fx:id naming convention:
     * fx:id="dashboard" -> dashboardController, etc.
     */
    @FXML
    private Node dashboard;
    @FXML
    private DashboardController dashboardController;

    @FXML
    private Node rooms;
    @FXML
    private RoomController roomsController;

    @FXML
    private Node customers;
    @FXML
    private CustomerController customersController;

    @FXML
    private Node bookings;
    @FXML
    private BookingController bookingsController;

    @FXML
    private Node billing;
    @FXML
    private BillingController billingController;

    /**
     * Called after FXML injection. Sets up tab-change listeners
     * to refresh each tab's data when it becomes active.
     */
    @FXML
    public void initialize() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            switch (newIdx.intValue()) {
                case 0 -> dashboardController.refresh();
                case 3 -> bookingsController.refresh();
                case 4 -> billingController.refresh();
            }
        });
    }
}
