package com.hotel.service;

import com.hotel.model.Customer;
import com.hotel.storage.DataStore;
import com.hotel.util.CustomerNotFoundException;
import com.hotel.util.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Customer operations.
 * Contains business logic — delegates persistence to the storage layer.
 */
public class CustomerService {

    private List<Customer> customers;

    /**
     * Constructs the CustomerService and loads existing customers from storage.
     */
    public CustomerService() {
        this.customers = DataStore.getCustomerStorage().loadAll();
    }

    /**
     * Returns all customers.
     *
     * @return list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customers;
    }

    /**
     * Finds a customer by their ID.
     *
     * @param customerId the customer ID to search for
     * @return the matching Customer
     * @throws CustomerNotFoundException if no customer with the given ID exists
     */
    public Customer findById(String customerId) throws CustomerNotFoundException {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found."));
    }

    /**
     * Searches customers by name or contact number (case-insensitive partial
     * match).
     *
     * @param query the search query
     * @return list of matching customers
     */
    public List<Customer> search(String query) {
        String lowerQuery = query.toLowerCase().trim();
        return customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery)
                        || c.getContactNumber().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new customer after validating the input.
     *
     * @param name          the full name
     * @param contactNumber the phone number
     * @param email         the email address
     * @param address       the address
     * @throws ValidationException if any field is invalid
     */
    public void addCustomer(String name, String contactNumber, String email, String address)
            throws ValidationException {
        validateCustomerFields(name, contactNumber, email, address);

        Customer customer = new Customer(name.trim(), contactNumber.trim(), email.trim(), address.trim());
        customers.add(customer);
        save();
    }

    /**
     * Updates an existing customer's details.
     *
     * @param customerId    the ID of the customer to update
     * @param name          the new name
     * @param contactNumber the new contact number
     * @param email         the new email
     * @param address       the new address
     * @throws CustomerNotFoundException if the customer is not found
     * @throws ValidationException       if any field is invalid
     */
    public void updateCustomer(String customerId, String name, String contactNumber,
            String email, String address)
            throws CustomerNotFoundException, ValidationException {
        validateCustomerFields(name, contactNumber, email, address);

        Customer customer = findById(customerId);
        customer.setName(name.trim());
        customer.setContactNumber(contactNumber.trim());
        customer.setEmail(email.trim());
        customer.setAddress(address.trim());
        save();
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param customerId the customer ID to delete
     * @throws CustomerNotFoundException if the customer is not found
     */
    public void deleteCustomer(String customerId) throws CustomerNotFoundException {
        Customer customer = findById(customerId);
        customers.remove(customer);
        save();
    }

    /**
     * Returns the total number of customers.
     *
     * @return total customer count
     */
    public long getTotalCustomers() {
        return customers.size();
    }

    /**
     * Reloads customers from storage.
     */
    public void reload() {
        this.customers = DataStore.getCustomerStorage().loadAll();
    }

    /**
     * Validates customer input fields.
     *
     * @param name          the name to validate
     * @param contactNumber the contact number to validate
     * @param email         the email to validate
     * @param address       the address to validate
     * @throws ValidationException if any field is invalid
     */
    private void validateCustomerFields(String name, String contactNumber, String email, String address)
            throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Customer name is required.");
        }
        if (name.matches(".*\\d.*")) {
            throw new ValidationException("Customer name cannot contain digits.");
        }
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            throw new ValidationException("Contact number is required.");
        }
        if (!contactNumber.trim().matches("^[0-9]{10}$")) {
            throw new ValidationException("Contact number must be exactly 10 digits.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Invalid email format.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Address is required.");
        }
    }

    /**
     * Persists the current list of customers to storage.
     */
    private void save() {
        DataStore.getCustomerStorage().saveAll(customers);
    }
}
