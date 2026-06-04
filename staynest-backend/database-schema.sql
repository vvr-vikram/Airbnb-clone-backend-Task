-- ============================================================================
-- StayNest Database Schema and Indian Context Mock Seed Data
-- Database Name: staynest_db
-- ============================================================================

CREATE DATABASE IF NOT EXISTS staynest_db;
USE staynest_db;

-- 1. Drop tables if they exist (for a clean setup)
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS property_availabilities;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS users;

-- 2. Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Properties Table
CREATE TABLE properties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(100) NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    host_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_property_host FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_properties_location (location),
    INDEX idx_properties_price (price_per_night)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Property Availabilities Table
CREATE TABLE property_availabilities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    available_from DATE NOT NULL,
    available_to DATE NOT NULL,
    CONSTRAINT fk_availability_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    INDEX idx_availabilities_dates (property_id, available_from, available_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Bookings Table
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_price DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_guest FOREIGN KEY (guest_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_bookings_dates (property_id, start_date, end_date, status),
    INDEX idx_bookings_guest (guest_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Reviews Table
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_guest FOREIGN KEY (guest_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_reviews_property (property_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- Seed Data (Indian Host, Guest, Stays, and Bookings Context)
-- ============================================================================

-- 1. Insert Users (Hosts & Guests)
INSERT INTO users (id, name, email, role, created_at) VALUES
(1, 'Aarav Sharma', 'aarav@staynest.in', 'HOST', NOW()),
(2, 'Priya Patel', 'priya@staynest.in', 'HOST', NOW()),
(3, 'Amit Verma', 'amit@staynest.in', 'HOST', NOW()),
(4, 'Ananya Iyer', 'ananya@staynest.in', 'GUEST', NOW()),
(5, 'Rohan Deshmukh', 'rohan@staynest.in', 'GUEST', NOW()),
(6, 'Sneha Reddy', 'sneha@staynest.in', 'GUEST', NOW());

-- 2. Insert Properties
INSERT INTO properties (id, title, description, location, price_per_night, host_id, created_at) VALUES
(1, 'Taj Luxury Villa', 'A premium sea-view luxury villa with a private infinity pool located in Candolim, Goa.', 'Goa', 15000.00, 1, NOW()),
(2, 'Ganga Heritage Homestay', 'A historic building offering beautiful sunrise views of the Ganga river in Varanasi.', 'Varanasi', 3500.00, 2, NOW()),
(3, 'Deccan Residency', 'Comfortable business and holiday apartment close to IT hubs in Gachibowli, Hyderabad.', 'Hyderabad', 4500.00, 3, NOW()),
(4, 'Jaipur Royal Palace', 'A heritage fort stay displaying traditional Rajasthani royalty and architectures.', 'Jaipur', 8500.00, 1, NOW()),
(5, 'Bengaluru Tech Hub Apartment', 'Modern, fully furnished flat with high speed wifi located in Indiranagar, Bengaluru.', 'Bengaluru', 5000.00, 2, NOW());

-- 3. Set Property Availabilities
INSERT INTO property_availabilities (id, property_id, available_from, available_to) VALUES
(1, 1, '2026-06-01', '2026-06-30'), -- Taj Luxury Villa available all June 2026
(2, 2, '2026-06-05', '2026-07-05'), -- Ganga Heritage available June 5 - July 5
(3, 3, '2026-06-10', '2026-06-25'), -- Deccan Residency available June 10 - June 25
(4, 4, '2026-06-01', '2026-06-15'); -- Jaipur Royal Palace available June 1 - June 15

-- 4. Set Historical/Existing Bookings
-- A completed stay (allows review validation)
INSERT INTO bookings (id, property_id, guest_id, start_date, end_date, total_price, status, created_at) VALUES
(1, 1, 5, '2026-05-15', '2026-05-20', 75000.00, 'COMPLETED', '2026-05-14 10:00:00');

-- Active bookings (blocks overlapping booking attempts)
INSERT INTO bookings (id, property_id, guest_id, start_date, end_date, total_price, status, created_at) VALUES
(2, 3, 4, '2026-06-10', '2026-06-12', 90000.00, 'CONFIRMED', NOW()), -- Deccan Residency booked June 10 - 12
(3, 1, 6, '2026-06-10', '2026-06-15', 75000.00, 'CONFIRMED', NOW()); -- Taj Luxury Villa booked June 10 - 15

-- 5. Set Initial Reviews
-- Rohan stayed at Taj Luxury Villa and left a review
INSERT INTO reviews (id, property_id, guest_id, rating, comment, created_at) VALUES
(1, 1, 5, 5, 'Stunning experience at the Taj Luxury Villa! The private pool and seaside views were world-class. Very friendly hospitality from host Aarav.', NOW());
