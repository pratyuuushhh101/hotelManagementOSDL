module com.hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;

    opens com.hotel to javafx.fxml;
    opens com.hotel.controller to javafx.fxml;
    opens com.hotel.model to com.google.gson, javafx.base;

    exports com.hotel;
    exports com.hotel.controller;
    exports com.hotel.model;
    exports com.hotel.service;
    exports com.hotel.storage;
    exports com.hotel.util;
}
