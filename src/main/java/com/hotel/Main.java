package com.hotel;

import com.hotel.controller.ServiceLocator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Main entry point for the Hotel Management System JavaFX application.
 * Initializes services, loads the main FXML layout, and applies styling.
 */
public class Main extends Application {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application: initializes services, loads FXML, applies CSS.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize all services before loading any FXML
        ServiceLocator.init();

        // Load main FXML
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        // Create scene and apply stylesheet
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        // Configure stage
        primaryStage.setTitle("🏨 Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
}
