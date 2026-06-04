# StayNest - Airbnb-Like Property Booking Backend System

StayNest is a robust, scalable backend system built with **Spring Boot 3**, **Spring Data JPA/Hibernate**, and **MySQL**. It is designed to manage property listings, availabilities, user interactions (Hosts and Guests), bookings, and reviews in an Airbnb-like model.

This project is tailored for development in **Eclipse** and uses **MySQL**. It uses Indian names, property contexts, and cities (Goa, Hyderabad, Jaipur, Varanasi, Bengaluru) to provide realistic test scenarios.

---

## Technical Stack

| Component | Technology |
|-----------|------------|
| **Core Framework** | Spring Boot 3.2.0 |
| **Java Version** | Java 17 |
| **Database** | MySQL 8.0+ |
| **ORM / JPA** | Hibernate & Spring Data JPA |
| **Build & Dependency Tool** | Maven 3.8+ |
| **API Documentation** | Springdoc OpenAPI / Swagger UI |
| **Boilerplate Reduction** | Lombok |
| **Unit Testing** | JUnit 5 & Mockito |

---

## Key Features

1. **User Registration & Roles** – Standardized signups for both **Hosts** (who list properties) and **Guests** (who book properties).
2. **Property Management** – Hosts can create, list, and update details (title, price, location) of apartments, villas, and stays.
3. **Availability Windows** – Hosts can specify date intervals (available from/to) during which properties are bookable.
4. **Advanced Property Search** – Guests can search and filter properties by location (case-insensitive search), price per night range, and average rating with built-in **pagination and sorting**.
5. **Overlapping Booking Prevention** – Core logic checks that the booking dates are within host availability ranges and do not overlap with any existing `CONFIRMED` or `REQUESTED` bookings.
6. **Auto-Confirmation Flow** – Valid bookings are automatically confirmed immediately upon creation.
7. **Pessimistic Concurrency Locking** – Acquires a `PESSIMISTIC_WRITE` lock (`SELECT ... FOR UPDATE`) on the property row during booking. This serializes validation and booking execution for that property, preventing race conditions (double bookings) under high-concurrency request loads.
8. **Review System** – Guests can leave ratings (1-5 stars) and comments *only* after their stay is completed (`COMPLETED` status). Guests can write at most one review per property.
9. **Host Analytics Dashboard** – Hosts can fetch property-specific metrics including total bookings count, total earnings, average rating, and counts of active/completed/cancelled bookings.

---

## Database Design & Schema

The database contains 5 core tables with optimized foreign keys and indexes.

### Entity Relationship Model

```
   ┌───────────┐
   │   users   │
   └─────┬─────┘
         │ (1)
         ├─────────────────────────────────────────┐
         │ (host_id)                               │ (guest_id)
         ▼ (N)                                     ▼ (N)
   ┌─────────────┐                           ┌──────────┐
   │ properties  ├──────────────────────────►│ bookings │
   └──────┬──────┘ (1)                       └────▲─────┘
          │ (1)                                   │
          │ (property_id)                         │ (property_id)
          ├────────────────────────┐              │
          ▼ (N)                    ▼ (N)          │
   ┌──────────────┐          ┌─────────────┐      │
   │availability  │          │   reviews   ├──────┘
   └──────────────┘          └─────────────┘
```

### Table Specifications & Indexes

1. **`users`**
   - `id`: `BIGINT` (Primary Key, Auto Increment)
   - `name`: `VARCHAR(100)` (Not Null)
   - `email`: `VARCHAR(100)` (Unique, Not Null)
   - `role`: `VARCHAR(20)` (Not Null - `HOST` or `GUEST`)
   - `created_at`: `TIMESTAMP` (Default current time)
   - *Index*: `idx_user_email` on `email` (Fast login/lookup)

2. **`properties`**
   - `id`: `BIGINT` (Primary Key, Auto Increment)
   - `title`: `VARCHAR(150)` (Not Null)
   - `description`: `TEXT`
   - `location`: `VARCHAR(100)` (Not Null)
   - `price_per_night`: `DECIMAL(10,2)` (Not Null)
   - `host_id`: `BIGINT` (Foreign Key referencing `users(id)`)
   - `created_at`: `TIMESTAMP`
   - *Index*: `idx_properties_location` on `location` (Optimizes search query filtering)
   - *Index*: `idx_properties_price` on `price_per_night` (Optimizes range filters)

3. **`property_availabilities`**
   - `id`: `BIGINT` (Primary Key, Auto Increment)
   - `property_id`: `BIGINT` (Foreign Key referencing `properties(id)` on delete cascade)
   - `available_from`: `DATE` (Not Null)
   - `available_to`: `DATE` (Not Null)
   - *Index*: `idx_availabilities_dates` on `(property_id, available_from, available_to)` (Speeds up checking if date ranges are available)

4. **`bookings`**
   - `id`: `BIGINT` (Primary Key, Auto Increment)
   - `property_id`: `BIGINT` (Foreign Key referencing `properties(id)`)
   - `guest_id`: `BIGINT` (Foreign Key referencing `users(id)`)
   - `start_date`: `DATE` (Not Null)
   - `end_date`: `DATE` (Not Null)
   - `total_price`: `DECIMAL(12,2)` (Not Null)
   - `status`: `VARCHAR(30)` (Not Null - `REQUESTED`, `CONFIRMED`, `CANCELLED`, `COMPLETED`)
   - `created_at`: `TIMESTAMP`
   - *Index*: `idx_bookings_dates` on `(property_id, start_date, end_date, status)` (Speeds up overlapping date range searches)
   - *Index*: `idx_bookings_guest` on `guest_id` (Speeds up fetching user booking history)

5. **`reviews`**
   - `id`: `BIGINT` (Primary Key, Auto Increment)
   - `property_id`: `BIGINT` (Foreign Key referencing `properties(id)` on delete cascade)
   - `guest_id`: `BIGINT` (Foreign Key referencing `users(id)`)
   - `rating`: `INT` (Not Null, 1 to 5)
   - `comment`: `TEXT`
   - `created_at`: `TIMESTAMP`
   - *Index*: `idx_reviews_property` on `property_id` (Speeds up calculating average ratings and review list queries)

---

## Setup & Setup Instructions

### 1. Database Creation
Start your MySQL Server and execute the schema initialization script. This will create `staynest_db` and seed it with Indian users, property listings, booking records, and reviews.
```bash
# Log in to MySQL
mysql -u root -p

# Execute the script
mysql -u root -p < database-schema.sql
```
*Note: The schema SQL is located in [database-schema.sql](file:///c:/Users/vikra.VIGNESHXBS-HP/eclipse-workspace/staynest-backend/database-schema.sql).*

### 2. Configure Database Credentials
Open `src/main/resources/application.yml` and check the datasource settings:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/staynest_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root          # Your MySQL username
    password: root          # Your MySQL password
```
Change these values to align with your Eclipse/MySQL password configurations.

### 3. Open in Eclipse IDE
1. Open Eclipse and choose a workspace.
2. Select **File** -> **Import** -> **Existing Maven Projects**.
3. Browse and select the `staynest-backend` directory as the root.
4. Click **Finish**. Eclipse will fetch the dependencies and configure the project.

#### Lombok Setup in Eclipse (Highly Important)
Because this project uses Lombok to remove boilerplate getter, setter, and builder methods, you need to ensure Eclipse is configured to recognize Lombok:
1. Locate your Eclipse installation directory (e.g. `C:\Users\<user>\eclipse\jee-...\eclipse`).
2. Double-check if Lombok is added. If not, download `lombok.jar` from `https://projectlombok.org/download`.
3. Run the jar: `java -jar lombok.jar`.
4. Point the Lombok installer to your Eclipse executable file (`eclipse.exe`), and select **Install / Update**.
5. Restart Eclipse and clean the project (**Project** -> **Clean**).

---

## Running the Application

### Running inside Eclipse
- Right-click the `StayNestApplication.java` file (located in `src/main/java/com/staynest/booking/StayNestApplication.java`).
- Select **Run As** -> **Java Application** or **Spring Boot App**.

### Running from Terminal (Maven Wrapper)
To run the server:
```bash
mvn spring-boot:run
```

To run unit tests:
```bash
mvn clean test
```

---

## API Testing with Swagger and Postman

### Swagger UI
Once started, the API documentation is fully generated and accessible at:
```
http://localhost:8080/api/v1/swagger-ui.html
```
Use this dashboard to run API calls directly from the browser.

### Postman Collection
A complete Postman Collection is supplied in the project folder: [POSTMAN_COLLECTION.json](file:///c:/Users/vikra.VIGNESHXBS-HP/eclipse-workspace/staynest-backend/POSTMAN_COLLECTION.json).
1. Open Postman.
2. Click **Import** -> Select **File** -> Upload `POSTMAN_COLLECTION.json`.
3. You will see a folder structure with pre-configured requests categorized into:
   - **User Management** (Registering hosts and guests)
   - **Property Management** (Creating, updating, adding availability, searching, fetching details, listing bookings, viewing statistics)
   - **Booking Transactions** (Submitting auto-confirmed bookings, checking overlap errors, completing stays, cancelling)
   - **Reviews Management** (Leaving reviews after stays)

---

## Core API Endpoints

### 1. User Endpoints
- `POST /api/v1/users` – Register new user
- `GET /api/v1/users/{id}` – Get user details

### 2. Property Endpoints
- `POST /api/v1/properties` – Create property listing (Host only)
- `PUT /api/v1/properties/{id}` – Update property listing (Host only)
- `POST /api/v1/properties/{id}/availabilities` – Set availability dates (Host only)
- `GET /api/v1/properties` – Search properties (With filters: `location`, `minPrice`, `maxPrice`, `minRating`, page parameters)
- `GET /api/v1/properties/{id}` – Get full details (Includes host profile, reviews list, availabilities, average rating)
- `GET /api/v1/properties/popular` – Fetch popular properties
- `GET /api/v1/properties/{id}/bookings` – View all bookings for a property (Host only)
- `GET /api/v1/properties/{id}/stats` – Fetch booking statistics (Host only)

### 3. Booking Endpoints
- `POST /api/v1/bookings` – Create booking (Auto-confirmed if valid)
- `PUT /api/v1/bookings/{id}/cancel` – Cancel booking (Requires query param `userId`)
- `PUT /api/v1/bookings/{id}/complete` – Complete stay
- `GET /api/v1/bookings/user/{userId}` – Get user booking history

### 4. Review Endpoints
- `POST /api/v1/reviews` – Submit a review after stay completion

---

## Concurrency Safety Details
To prevent race conditions where multiple users try to book the same property for overlapping dates at the exact same moment:
1. When a transaction requests a booking, the system calls `propertyRepository.findByIdWithWriteLock(propertyId)`.
2. This runs `SELECT ... FROM properties WHERE id = ? FOR UPDATE` which locks the property row in the database.
3. Any other transaction attempting to load this property for locking is blocked until the first transaction commits or rolls back.
4. The first transaction verifies availability, verifies overlap, inserts the new booking, and commits.
5. The blocked transaction then resumes, loads the updated booking list, discovers the overlap, and safely fails with a `409 Conflict` status, avoiding double booking.
