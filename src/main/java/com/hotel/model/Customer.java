package com.hotel.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a hotel customer with personal contact information.
 * This is a plain data model with no UI or I/O dependencies.
 */
public class Customer {

    private String customerId;
    private String name;
    private String contactNumber;
    private String email;
    private String address;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Customer() {
    }

    /**
     * Creates a new Customer with a generated UUID and the specified details.
     *
     * @param name          the full name of the customer
     * @param contactNumber the phone/contact number
     * @param email         the email address
     * @param address       the residential address
     */
    public Customer(String name, String contactNumber, String email, String address) {
        this.customerId = UUID.randomUUID().toString();
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    /**
     * Returns the unique customer identifier.
     *
     * @return the customer ID (UUID string)
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer identifier.
     *
     * @param customerId the customer ID to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the customer's full name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customer's contact number.
     *
     * @return the contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the customer's contact number.
     *
     * @param contactNumber the contact number to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Returns the customer's email address.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the customer's address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the customer's address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return name + " (" + contactNumber + ")";
    }
}
