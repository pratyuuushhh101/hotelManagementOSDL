# 🏨 Hotel Management System

A production-grade **JavaFX 21** desktop application for managing hotel operations —
rooms, customers, bookings, and billing — with persistent JSON file storage.

---

## Features

| Feature             | Description                                                |
|---------------------|------------------------------------------------------------|
| **Room Management** | Add, edit, delete rooms with type/price; filter by availability |
| **Customer Management** | Register customers with validated contact info; search live |
| **Booking System**  | Book rooms with date pickers; prevent double-booking; checkout |
| **Billing**         | Auto-calculate bills; preview formatted summary; export to `.txt` |
| **Dashboard**       | Summary cards (total rooms, available, active bookings, revenue) |
| **Persistent Storage** | JSON files via Gson — `data/rooms.json`, `bookings.json`, `customers.json` |
| **Dark Theme UI**   | Custom CSS with gold accents, hover effects, alternating table rows |
| **Input Validation**| Inline red error labels, phone/email format checks, date logic |
| **Context Menus**   | Right-click Edit / Delete / View on all table rows |
| **Search & Filter** | Live search bars above every table; availability toggle for rooms |

---

## Tech Stack

- **Java 21**
- **JavaFX 21** (via OpenJFX Maven plugin)
- **Maven** (build tool)
- **Gson 2.10.1** (JSON serialization)
- **FXML** (Scene Builder-compatible layouts)

---

## Project Structure

```
src/main/java/com/hotel/
├── Main.java                     # Application entry point
├── controller/
│   ├── MainController.java       # TabPane controller
│   ├── DashboardController.java  # Dashboard summary cards
│   ├── RoomController.java       # Room CRUD
│   ├── CustomerController.java   # Customer CRUD
│   ├── BookingController.java    # Booking & checkout
│   ├── BillingController.java    # Bill generation & export
│   └── ServiceLocator.java       # Service singleton provider
├── model/
│   ├── Room.java                 # Room data model
│   ├── RoomType.java             # SINGLE / DOUBLE / DELUXE enum
│   ├── Customer.java             # Customer data model
│   ├── Booking.java              # Booking data model
│   └── BookingStatus.java        # ACTIVE / CHECKED_OUT enum
├── service/
│   ├── RoomService.java          # Room business logic
│   ├── CustomerService.java      # Customer business logic
│   ├── BookingService.java       # Booking business logic
│   └── BillingService.java       # Bill calculation & export
├── storage/
│   ├── JsonStorage.java          # Generic JSON file I/O
│   └── DataStore.java            # Typed storage factory
└── util/
    ├── RoomAlreadyBookedException.java
    ├── CustomerNotFoundException.java
    ├── RoomNotFoundException.java
    ├── BookingNotFoundException.java
    └── ValidationException.java

src/main/resources/
├── fxml/
│   ├── main.fxml
│   ├── dashboard.fxml
│   ├── rooms.fxml
│   ├── customers.fxml
│   ├── bookings.fxml
│   └── billing.fxml
├── css/
│   └── style.css
└── images/
```

---

## Prerequisites

- **JDK 21** or later
- **Maven 3.8+**

---

## Build & Run

```bash
# Compile and launch
mvn clean javafx:run
```

The application will open a 1200×750 window with the Hotel Management dashboard.

---

## Data Persistence

All data is stored as JSON files in the `data/` directory (created automatically):

- `data/rooms.json`
- `data/customers.json`
- `data/bookings.json`

Missing or corrupted files are handled gracefully — the app starts with empty data.

---

## Architecture

The project follows strict separation of concerns:

| Layer        | Responsibility                                |
|--------------|-----------------------------------------------|
| **Model**    | Plain data classes — no UI or I/O code         |
| **Service**  | Business logic — no file I/O or JavaFX code    |
| **Storage**  | File I/O only — reads/writes JSON via Gson     |
| **Controller** | FXML controllers — bridges UI to services    |
| **Util**     | Custom exceptions                             |

---

## License

This project is provided for educational and demonstration purposes.
