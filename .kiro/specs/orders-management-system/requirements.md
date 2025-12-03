# Requirements Document

## Introduction

The Orders Management System is a Spring Boot web application that enables users to manage order records through a web interface. The system provides full CRUD (Create, Read, Update, Delete) functionality for orders, with data persistence using an H2 in-memory database. Users can interact with orders through both a web UI and RESTful API endpoints.

## Glossary

- **System**: The Orders Management System application
- **Order**: A record containing order details including items, date, time, quantity, and availability status
- **User**: A person interacting with the system through the web interface
- **REST API**: RESTful web service endpoints for programmatic access to order data
- **H2 Database**: An in-memory relational database used for data persistence
- **Web UI**: The HTML-based user interface for managing orders
- **Order ID**: A unique auto-generated identifier for each order

## Requirements

### Requirement 1

**User Story:** As a user, I want to view all orders in a list, so that I can see what orders exist in the system.

#### Acceptance Criteria

1. WHEN the user navigates to the home page, THEN the System SHALL display all orders sorted by date
2. WHEN displaying orders, THEN the System SHALL show the item names and dates in a table format
3. WHEN the user clicks on an order item name, THEN the System SHALL display detailed information including time, quantity, and availability status
4. WHEN the user clicks on an expanded order again, THEN the System SHALL collapse the detailed information

### Requirement 2

**User Story:** As a user, I want to add new orders through a form, so that I can record new order information in the system.

#### Acceptance Criteria

1. WHEN the user navigates to the home page, THEN the System SHALL display a form with fields for items, date, time, quantity, and availability
2. WHEN the user submits the form with valid data, THEN the System SHALL create a new order record in the database
3. WHEN the user submits the form with an empty items field, THEN the System SHALL prevent submission and require the field to be filled
4. WHEN a new order is created, THEN the System SHALL display the updated order list including the new order
5. WHEN a new order is created, THEN the System SHALL assign a unique auto-incremented Order ID

### Requirement 3

**User Story:** As a user, I want to delete orders, so that I can remove orders that are no longer needed.

#### Acceptance Criteria

1. WHEN the user clicks the delete link for an order, THEN the System SHALL remove that order from the database
2. WHEN an order is deleted, THEN the System SHALL refresh the page and display the updated order list
3. WHEN an order is deleted, THEN the System SHALL not display that order in subsequent views

### Requirement 4

**User Story:** As a user, I want to edit existing orders, so that I can correct or update order information.

#### Acceptance Criteria

1. WHEN the user clicks the edit link for an order, THEN the System SHALL populate the form with that order's current data
2. WHEN the form is populated with existing order data, THEN the System SHALL delete the original order record
3. WHEN the user submits the edited form, THEN the System SHALL create a new order record with the updated information
4. WHEN an order is edited, THEN the System SHALL display the updated order list

### Requirement 5

**User Story:** As a developer, I want to access order data through REST API endpoints, so that I can integrate with other systems or test functionality programmatically.

#### Acceptance Criteria

1. WHEN a GET request is made to /orders, THEN the System SHALL return all orders as JSON
2. WHEN a POST request is made to /orders with valid order data, THEN the System SHALL create a new order and return the order URL
3. WHEN a GET request is made to /orders/{orderId}, THEN the System SHALL return the specific order as JSON
4. WHEN a PUT request is made to /orders/{orderId} with updated data, THEN the System SHALL update the order and return a confirmation message
5. WHEN a DELETE request is made to /orders/{orderId}, THEN the System SHALL delete the order and return a confirmation message

### Requirement 6

**User Story:** As a system administrator, I want to access the H2 database console, so that I can verify data integrity and troubleshoot issues.

#### Acceptance Criteria

1. WHEN the application is running, THEN the System SHALL enable the H2 console at /h2-console
2. WHEN accessing the H2 console, THEN the System SHALL allow connection to the in-memory database using jdbc:h2:mem:testdb
3. WHEN connected to the database, THEN the System SHALL display the orders table with all stored records

### Requirement 7

**User Story:** As a system, I want to initialize the database with schema and sample data on startup, so that the application is ready to use immediately.

#### Acceptance Criteria

1. WHEN the application starts, THEN the System SHALL create the orders table with columns for orderId, localD, localT, items, quantity, and onHand
2. WHEN the application starts, THEN the System SHALL set orderId as the primary key with auto-increment
3. WHEN the application starts, THEN the System SHALL insert sample order data from data.sql
4. WHEN the database is initialized, THEN the System SHALL be ready to accept queries and modifications

### Requirement 8

**User Story:** As a user, I want order details to load dynamically without page refresh, so that I can have a smooth browsing experience.

#### Acceptance Criteria

1. WHEN the user clicks on an order item name, THEN the System SHALL fetch order details using JavaScript without refreshing the page
2. WHEN order details are fetched, THEN the System SHALL retrieve data from the REST API endpoint
3. WHEN order details are received, THEN the System SHALL display them in the same table row below the order name
4. WHEN the user collapses order details, THEN the System SHALL clear the displayed information without making additional API calls
