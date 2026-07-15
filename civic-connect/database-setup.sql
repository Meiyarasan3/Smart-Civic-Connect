-- ============================================
-- Civic Connect — Database Setup
-- Run this ONCE before starting the server
-- ============================================

-- Create database
CREATE DATABASE IF NOT EXISTS civic_connect_db;
USE civic_connect_db;

-- Tables are auto-created by Hibernate (ddl-auto=update)
-- This script is for manual setup or reference only.

-- ── Users ──
CREATE TABLE IF NOT EXISTS users (
    user_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    phone      VARCHAR(15),
    created_at DATETIME
);

-- ── Complaints ──
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    citizen_id   BIGINT       NOT NULL,
    title        VARCHAR(200) NOT NULL,
    description  TEXT         NOT NULL,
    category     VARCHAR(30)  NOT NULL,
    location     VARCHAR(300),
    image_url    VARCHAR(500),
    status       VARCHAR(20)  NOT NULL DEFAULT 'SUBMITTED',
    created_at   DATETIME,
    updated_at   DATETIME,
    FOREIGN KEY (citizen_id) REFERENCES users(user_id)
);

-- ── Assignments ──
CREATE TABLE IF NOT EXISTS assignments (
    assignment_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id    BIGINT       NOT NULL UNIQUE,
    department_name VARCHAR(100) NOT NULL,
    assigned_by     BIGINT       NOT NULL,
    assigned_to     BIGINT,
    assigned_date   DATETIME,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id),
    FOREIGN KEY (assigned_by)  REFERENCES users(user_id),
    FOREIGN KEY (assigned_to)  REFERENCES users(user_id)
);

-- ── Complaint Updates ──
CREATE TABLE IF NOT EXISTS complaint_updates (
    update_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id    BIGINT      NOT NULL,
    officer_id      BIGINT      NOT NULL,
    remarks         TEXT,
    status          VARCHAR(20) NOT NULL,
    proof_image_url VARCHAR(500),
    update_date     DATETIME,
    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id),
    FOREIGN KEY (officer_id)   REFERENCES users(user_id)
);

-- ── Sample Test Data (Optional) ──
-- Uncomment to seed test users (passwords are BCrypt of "password123")

-- INSERT INTO users (name, email, password, role, created_at) VALUES
-- ('Meiyarasan', 'citizen@test.com', '$2a$10$N5IjKw8gCRm4JYRf0H6JQ.6oKhI5H9sS5sJ5b5PfLzP6YZfF5v6Iy', 'CITIZEN', NOW()),
-- ('Admin Officer', 'admin@test.com', '$2a$10$N5IjKw8gCRm4JYRf0H6JQ.6oKhI5H9sS5sJ5b5PfLzP6YZfF5v6Iy', 'ADMIN', NOW()),
-- ('Dept Officer', 'dept@test.com', '$2a$10$N5IjKw8gCRm4JYRf0H6JQ.6oKhI5H9sS5sJ5b5PfLzP6YZfF5v6Iy', 'DEPARTMENT', NOW());
