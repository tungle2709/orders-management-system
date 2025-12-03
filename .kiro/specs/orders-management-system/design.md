# Design Document - Orders Management System

## Overview

The Orders Management System is a Spring Boot application following the MVC (Model-View-Controller) pattern with RESTful API capabilities. The architecture separates concerns into distinct layers: data access (repository), business logic (REST controller), presentation logic (home controller), and view (Thymeleaf templates with JavaScript).

The system uses an H2 in-memory database for data persistence, Spring JDBC for database operations, and Thymeleaf for server-side rendering. Client-side JavaScript enhances the user experience by fetching order details dynamically via REST API calls.

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (index.html + JavaScript)              │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Controller Layer                  │
│  ┌──────────────┐  ┌─────────────────┐ │
│  │HomeController│  │OrdersController │ │
│  │  (MVC)       │  │  (REST API)     │ │
│  └──────────────┘  └─────────────────┘ │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Data Access Layer                 │
│       (DatabaseAccess)                  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│       Database Layer                    │
│       (H2 In-Memory Database)           │
└─────────────────────────────────────────┘
```

### Technology Stack

- **Framework**: Spring Boot 2.x+
- **Database**: H2 (in-memory)
- **Data Access**: Spring JDBC with NamedParameterJdbcTemplate
- **View Template**: Thymeleaf
- **Client-Side**: JavaScript (Fetch API)
- **Build Tool**: Maven or Gradle
- **Java Version**: Java 11+

## Components and Interfaces

### 1. Orders (POJO/Entity)

**Purpose**: Represents an order entity with all its attributes.

**Attributes**:
- `orderId` (Integer): Auto-generated unique identifier
- `items` (String): Comma-separated list of items in the order
- `localD` (LocalDate): Order date
- `localT` (LocalTime): Order time
- `quantity` (Integer): Number of items
- `onHand` (Boolean): Availability status

**Annotations**:
- `@Data`: Lombok annotation for getters/setters
- `@NoArgsConstructor`: Generates no-argument constructor
- `@RequiredArgsConstructor`: Generates constructor for @NonNull fields
- `@NonNull` on items: Marks items as required field
- `@DateTimeFormat`: Specifies date/time format patterns

### 2. DatabaseAccess (Repository)

**Purpose**: Handles all database operations using JDBC.

**Dependencies**:
- `NamedParameterJdbcTemplate`: For parameterized SQL queries

**Methods**:

```java
List<Orders> findAllOrders()
```
- Returns all orders sorted by date
- Uses SELECT query with ORDER BY clause

```java
void save(Orders orders)
```
- Inserts new order into database
- Uses KeyHolder for auto-generated ID

```java
void deleteById(Long orderId)
```
- Deletes order by ID
- Uses parameterized DELETE query

```java
Orders findByOrderId(Long orderId)
```
- Retrieves single order by ID
- Returns Orders object or throws exception if not found

```java
void updateIndividualOrder(Long orderId, Orders orders)
```
- Updates order items field
- Uses parameterized UPDATE query

### 3. OrdersController (REST Controller)

**Purpose**: Provides RESTful API endpoints for order management.

**Base Path**: `/orders`

**Endpoints**:

```java
GET /orders
```
- Returns: List<Orders> (JSON)
- Description: Retrieves all orders

```java
POST /orders
```
- Consumes: application/json
- Body: Orders object
- Returns: String (URL of created resource)
- Description: Creates new order

```java
GET /orders/{orderId}
```
- Path Variable: orderId
- Returns: Orders (JSON)
- Description: Retrieves specific order

```java
PUT /orders/{orderId}
```
- Path Variable: orderId
- Body: Orders object
- Returns: String ("Updated")
- Description: Updates order

```java
DELETE /orders/{orderId}
```
- Path Variable: orderId
- Returns: String ("Order has been deleted")
- Description: Deletes order

### 4. HomeController (MVC Controller)

**Purpose**: Handles web UI requests and coordinates between view and REST API.

**Dependencies**:
- `RestTemplate`: For internal REST API calls
- `Model`: For passing data to views

**Endpoints**:

```java
GET /
```
- Fetches all orders via REST API
- Adds ordersList and empty Orders object to model
- Returns: "index" view

```java
POST /insertOrders
```
- Receives form data as Orders object
- Posts to REST API
- Returns: "index" view

```java
GET /insertOrders
```
- Redirects to home page
- Refreshes order list

```java
GET /deleteOrders/{orderId}
```
- Calls REST API delete endpoint
- Redirects to home page

```java
GET /editOrders/{orderId}
```
- Fetches order via REST API
- Deletes original order
- Populates form with order data
- Returns: "index" view

### 5. View Layer (index.html)

**Purpose**: Provides user interface for order management.

**Components**:

**Order List Table**:
- Displays items and dates
- Clickable item names trigger JavaScript
- Dynamic detail expansion area
- Delete and Edit links for each order

**Order Form**:
- Hidden field for orderId
- Text input for items (required)
- Date picker for localD
- Time picker for localT
- Number input for quantity
- Checkbox for onHand status
- Submit button

**JavaScript (script.js)**:
- `getOrders(orderId)` function
- Fetches order details via REST API
- Toggles detail display
- Uses Fetch API for asynchronous requests

## Data Models

### Database Schema

```sql
CREATE TABLE orders (
    orderId INT PRIMARY KEY AUTO_INCREMENT,
    localD DATE,
    localT TIME,
    items VARCHAR(100),
    quantity INT,
    onHand BOOLEAN
);
```

### Sample Data

```sql
INSERT INTO orders(localD, localT, items, quantity, onHand) 
VALUES('2022-02-28', '23:59', 'GiftCard,Car key', 5, false);
```

### Configuration

**application.properties**:
```properties
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
```

## Correct
ness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Orders sorted by date

*For any* collection of orders in the database, when retrieving all orders, the returned list should be sorted in ascending order by the localD (date) field.

**Validates: Requirements 1.1**

### Property 2: Complete order information retrieval

*For any* order ID that exists in the database, retrieving that order should return an object containing all required fields: orderId, items, localD, localT, quantity, and onHand with their correct values.

**Validates: Requirements 1.3**

### Property 3: Save-retrieve round trip

*For any* valid order object with non-null items field, saving the order to the database and then retrieving all orders should result in a list that contains an order with equivalent field values (excluding the auto-generated orderId).

**Validates: Requirements 2.2, 2.4**

### Property 4: Empty items validation

*For any* order object where the items field is null or empty, attempting to save that order should either fail validation or be prevented by the system constraints.

**Validates: Requirements 2.3**

### Property 5: Unique auto-increment IDs

*For any* sequence of order creations, each successfully saved order should receive a unique orderId value, and these values should be strictly increasing (each new orderId > previous orderId).

**Validates: Requirements 2.5**

### Property 6: Delete removes order

*For any* order ID that exists in the database, after deleting that order, attempting to retrieve it by ID should fail (throw exception or return null), and the order should not appear in the list of all orders.

**Validates: Requirements 3.1, 3.3**

### Property 7: Update modifies order data

*For any* existing order and any valid update data (new items value), after performing an update operation on that order ID, retrieving the order should return the updated items value while preserving the orderId.

**Validates: Requirements 4.3**

## Error Handling

### Database Errors

**Connection Failures**:
- Spring Boot will fail to start if database initialization fails
- H2 in-memory database is created on startup, minimal connection issues expected

**Query Failures**:
- SQL exceptions should be logged
- Repository methods may throw DataAccessException
- Controllers should handle exceptions gracefully

### Validation Errors

**Required Fields**:
- Items field marked with @NonNull annotation
- HTML form has required attribute on items input
- Backend should validate before database insertion

**Data Type Mismatches**:
- Spring's type conversion handles form data to Java types
- Invalid date/time formats should be caught by @DateTimeFormat
- Number format exceptions for quantity field

### REST API Errors

**Not Found (404)**:
- When requesting order by non-existent ID
- Should return appropriate HTTP status code

**Bad Request (400)**:
- When POST/PUT request has invalid JSON
- When required fields are missing

**Method Not Allowed (405)**:
- When using wrong HTTP method on endpoint

### Edge Cases

**Empty Database**:
- findAllOrders() should return empty list, not null
- UI should handle empty list gracefully

**Concurrent Access**:
- H2 in-memory database handles basic concurrency
- Auto-increment IDs prevent duplicate ID issues
- No explicit locking mechanism needed for this application

**Large Item Strings**:
- VARCHAR(100) limit on items field
- Should validate or truncate strings exceeding limit

## Testing Strategy

### Unit Testing

The system will use **JUnit 5** for unit testing with **Spring Boot Test** framework for integration testing.

**Unit Test Coverage**:

1. **DatabaseAccess Tests**:
   - Test findAllOrders returns correct data
   - Test save inserts record correctly
   - Test findByOrderId retrieves correct order
   - Test deleteById removes record
   - Test updateIndividualOrder modifies data
   - Test edge case: findByOrderId with non-existent ID
   - Test edge case: empty database returns empty list

2. **Controller Tests**:
   - Test REST endpoints return correct HTTP status codes
   - Test JSON serialization/deserialization
   - Test HomeController model attributes
   - Test redirect behaviors

3. **Orders POJO Tests**:
   - Test Lombok-generated methods work correctly
   - Test date/time formatting

### Property-Based Testing

The system will use **jqwik** (a property-based testing framework for Java) to verify universal properties.

**Configuration**:
- Each property-based test will run a minimum of 100 iterations
- Tests will use random data generation for orders with various dates, times, items, and quantities

**Property-Based Test Requirements**:

1. Each correctness property listed above must be implemented as a property-based test
2. Each test must be tagged with a comment in this format: `**Feature: orders-management-system, Property {number}: {property_text}**`
3. Tests should generate random valid Orders objects with:
   - Random item strings (1-100 characters)
   - Random dates (within reasonable range)
   - Random times
   - Random quantities (positive integers)
   - Random boolean values for onHand

**Property Test Coverage**:

1. **Property 1 Test**: Generate random collections of orders with different dates, save them, retrieve all, verify sorted order
2. **Property 2 Test**: Generate random order, save it, retrieve by ID, verify all fields match
3. **Property 3 Test**: Generate random order, save it, retrieve all orders, verify the order exists with matching fields
4. **Property 4 Test**: Generate orders with null/empty items, verify save fails or is prevented
5. **Property 5 Test**: Generate sequence of random orders, save them, verify IDs are unique and increasing
6. **Property 6 Test**: Generate random order, save it, delete it, verify it cannot be retrieved
7. **Property 7 Test**: Generate random order, save it, update with new items value, verify retrieval shows updated value

### Integration Testing

**End-to-End Tests**:
- Test complete workflows: create → read → update → delete
- Test REST API with actual HTTP requests using MockMvc
- Test database initialization with schema.sql and data.sql
- Verify H2 console accessibility

**Test Database**:
- Use H2 in-memory database for tests (same as production)
- Each test should have isolated database state
- Use @Transactional or @DirtiesContext for test isolation

### Manual Testing

**Browser Testing**:
- Verify form submission works correctly
- Test JavaScript order detail expansion/collapse
- Verify delete and edit links work
- Test with different browsers (Chrome, Firefox, Safari)

**API Testing**:
- Use Postman or curl to test REST endpoints
- Verify JSON responses are correctly formatted
- Test all HTTP methods (GET, POST, PUT, DELETE)

## Implementation Notes

### Dependencies

**Required Maven/Gradle Dependencies**:
- spring-boot-starter-web
- spring-boot-starter-jdbc
- spring-boot-starter-thymeleaf
- h2database
- lombok
- spring-boot-starter-test (for testing)
- jqwik (for property-based testing)

### Configuration

**Application Properties**:
- Enable H2 console
- Configure H2 in-memory database URL
- Set up SQL script locations (schema.sql, data.sql)

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/orders/
│   │       ├── OrdersApplication.java
│   │       ├── model/
│   │       │   └── Orders.java
│   │       ├── repository/
│   │       │   └── DatabaseAccess.java
│   │       └── controller/
│   │           ├── OrdersController.java
│   │           └── HomeController.java
│   └── resources/
│       ├── application.properties
│       ├── schema.sql
│       ├── data.sql
│       ├── templates/
│       │   └── index.html
│       └── static/
│           └── js/
│               └── script.js
└── test/
    └── java/
        └── com/example/orders/
            ├── repository/
            │   └── DatabaseAccessTest.java
            ├── controller/
            │   ├── OrdersControllerTest.java
            │   └── HomeControllerTest.java
            └── properties/
                └── OrdersPropertyTest.java
```

### RestTemplate Configuration

The HomeController requires a RestTemplate bean to make internal REST API calls. This should be configured in the main application class:

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

### Lombok Setup

Ensure Lombok is properly configured in the IDE and build tool. Annotation processing must be enabled for Lombok annotations to work correctly.
