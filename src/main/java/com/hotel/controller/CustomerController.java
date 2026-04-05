package com.hotel.controller;

import com.hotel.model.Customer;
import com.hotel.service.CustomerService;
import com.hotel.util.CustomerNotFoundException;
import com.hotel.util.ValidationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * Controller for the Customers tab (customers.fxml).
 * Manages customer CRUD, search, and table display.
 */
public class CustomerController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;
    @FXML
    private Label nameError;
    @FXML
    private Label contactError;
    @FXML
    private Label emailError;
    @FXML
    private Label addressError;
    @FXML
    private Button addCustomerBtn;
    @FXML
    private Button updateCustomerBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button deleteCustomerBtn;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> colId;
    @FXML
    private TableColumn<Customer, String> colName;
    @FXML
    private TableColumn<Customer, String> colContact;
    @FXML
    private TableColumn<Customer, String> colEmail;
    @FXML
    private TableColumn<Customer, String> colAddress;

    private CustomerService customerService;
    private ObservableList<Customer> customerList;
    private FilteredList<Customer> filteredCustomers;
    private String selectedCustomerId = null;

    /**
     * Called after FXML injection. Sets up the form, table, context menu, and
     * listeners.
     */
    @FXML
    public void initialize() {
        customerService = ServiceLocator.getCustomerService();

        // Setup table columns
        colId.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getCustomerId().substring(0, 8) + "..."));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colContact.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContactNumber()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colAddress.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));

        // Load data
        loadCustomers();

        // Search filtering
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // Table selection -> fill form
        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillForm(newVal);
            }
        });

        // Context menu
        setupContextMenu();
    }

    /**
     * Loads customers into the table with a filtered wrapper.
     */
    private void loadCustomers() {
        customerList = FXCollections.observableArrayList(customerService.getAllCustomers());
        filteredCustomers = new FilteredList<>(customerList, p -> true);
        customersTable.setItems(filteredCustomers);
    }

    /**
     * Applies the search filter.
     */
    private void applyFilter() {
        String query = searchField.getText().toLowerCase().trim();
        filteredCustomers.setPredicate(customer -> {
            if (query.isEmpty())
                return true;
            return customer.getName().toLowerCase().contains(query)
                    || customer.getContactNumber().contains(query)
                    || customer.getEmail().toLowerCase().contains(query);
        });
    }

    /**
     * Fills the form with data from the selected customer.
     *
     * @param customer the selected customer
     */
    private void fillForm(Customer customer) {
        selectedCustomerId = customer.getCustomerId();
        nameField.setText(customer.getName());
        contactField.setText(customer.getContactNumber());
        emailField.setText(customer.getEmail());
        addressField.setText(customer.getAddress());
        clearErrors();
    }

    /**
     * Sets up the right-click context menu on table rows.
     */
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("✏ Edit");
        editItem.setOnAction(e -> {
            Customer selected = customersTable.getSelectionModel().getSelectedItem();
            if (selected != null)
                fillForm(selected);
        });

        MenuItem deleteItem = new MenuItem("🗑 Delete");
        deleteItem.setOnAction(e -> handleDeleteCustomer());

        MenuItem viewItem = new MenuItem("👁 View Details");
        viewItem.setOnAction(e -> {
            Customer selected = customersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showInfo("Customer Details",
                        "ID: " + selected.getCustomerId() + "\n"
                                + "Name: " + selected.getName() + "\n"
                                + "Contact: " + selected.getContactNumber() + "\n"
                                + "Email: " + selected.getEmail() + "\n"
                                + "Address: " + selected.getAddress());
            }
        });

        contextMenu.getItems().addAll(editItem, deleteItem, viewItem);
        customersTable.setContextMenu(contextMenu);
    }

    /**
     * Handles the Add Customer button click.
     */
    @FXML
    private void handleAddCustomer() {
        clearErrors();
        try {
            customerService.addCustomer(
                    nameField.getText(),
                    contactField.getText(),
                    emailField.getText(),
                    addressField.getText());
            showSuccess("Customer added successfully!");
            handleClear();
            refreshTable();
        } catch (ValidationException e) {
            showValidationError(e.getMessage());
        }
    }

    /**
     * Handles the Update Customer button click.
     */
    @FXML
    private void handleUpdateCustomer() {
        clearErrors();
        if (selectedCustomerId == null) {
            showError("No Selection", "Please select a customer from the table to update.");
            return;
        }
        try {
            customerService.updateCustomer(
                    selectedCustomerId,
                    nameField.getText(),
                    contactField.getText(),
                    emailField.getText(),
                    addressField.getText());
            showSuccess("Customer updated successfully!");
            handleClear();
            refreshTable();
        } catch (CustomerNotFoundException | ValidationException e) {
            showError("Update Failed", e.getMessage());
        }
    }

    /**
     * Handles the Delete Customer button click.
     */
    @FXML
    private void handleDeleteCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a customer to delete.");
            return;
        }

        Optional<ButtonType> result = showConfirm(
                "Delete Customer", "Are you sure you want to delete " + selected.getName() + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                customerService.deleteCustomer(selected.getCustomerId());
                showSuccess("Customer deleted.");
                handleClear();
                refreshTable();
            } catch (CustomerNotFoundException e) {
                showError("Delete Failed", e.getMessage());
            }
        }
    }

    /**
     * Clears all form fields and selections.
     */
    @FXML
    private void handleClear() {
        selectedCustomerId = null;
        nameField.setText("");
        contactField.setText("");
        emailField.setText("");
        addressField.setText("");
        clearErrors();
        customersTable.getSelectionModel().clearSelection();
    }

    /**
     * Refreshes the table from the service data.
     */
    private void refreshTable() {
        customerList.setAll(customerService.getAllCustomers());
        applyFilter();
    }

    /**
     * Clears all inline error labels.
     */
    private void clearErrors() {
        nameError.setText("");
        contactError.setText("");
        emailError.setText("");
        addressError.setText("");
    }

    /**
     * Routes a validation error to the appropriate inline label.
     *
     * @param message the error message
     */
    private void showValidationError(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("name")) {
            nameError.setText(message);
        } else if (lower.contains("contact") || lower.contains("phone")) {
            contactError.setText(message);
        } else if (lower.contains("email")) {
            emailError.setText(message);
        } else if (lower.contains("address")) {
            addressError.setText(message);
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
