package com.hotel.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic JSON file storage handler using Gson.
 * Responsible for reading and writing lists of objects to JSON files.
 * This class handles only I/O — no business logic.
 *
 * @param <T> the type of objects to store
 */
public class JsonStorage<T> {

    private static final String DATA_DIR = "data";
    private final String fileName;
    private final Type type;
    private final Gson gson;

    /**
     * Creates a new JsonStorage for the given file and type.
     *
     * @param fileName the JSON file name (e.g., "rooms.json")
     * @param type     the Gson Type for deserialization (use TypeToken)
     */
    public JsonStorage(String fileName, Type type) {
        this.fileName = fileName;
        this.type = type;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        ensureDataDirectory();
    }

    /**
     * Ensures the data directory exists on disk.
     */
    private void ensureDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    /**
     * Loads all objects from the JSON file.
     * Returns an empty list if the file is missing, empty, or corrupt.
     *
     * @return the list of objects loaded from disk
     */
    public List<T> loadAll() {
        Path filePath = Paths.get(DATA_DIR, fileName);
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            List<T> list = gson.fromJson(reader, type);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Warning: Could not read " + fileName + ". Starting with empty data. Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves all objects to the JSON file, overwriting the existing content.
     *
     * @param items the list of objects to persist
     */
    public void saveAll(List<T> items) {
        Path filePath = Paths.get(DATA_DIR, fileName);
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            System.err.println("Error: Could not write to " + fileName + ": " + e.getMessage());
        }
    }
}
