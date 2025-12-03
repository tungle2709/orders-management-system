# Orders Management System

A Spring Boot web application for managing orders with full CRUD functionality, REST API, and dynamic web interface.

## Features

- **Web UI**: User-friendly interface for managing orders
- **REST API**: Full RESTful API for programmatic access
- **H2 Database**: In-memory database with console access
- **Dynamic Details**: JavaScript-powered order detail expansion
- **CRUD Operations**: Create, Read, Update, and Delete orders

## Technologies

- Java 11
- Spring Boot 2.7.18
- Spring JDBC
- Thymeleaf
- H2 Database
- Maven
- JavaScript (Fetch API)

## Project Structure

```
src/
├── main/
│   ├── java/com/example/orders/
│   │   ├── OrdersApplication.java          # Main application class
│   │   ├── model/
│   │   │   └── Orders.java                 # Order entity
│   │   ├── repository/
│   │   │   └── DatabaseAccess.java         # Data access layer
│   │   └── controller/
│   │       ├── OrdersController.java       # REST API controller
│   │       └── HomeController.java         # Web UI controller
│   └── resources/
│       ├── application.properties          # Configuration
│       ├── schema.sql                      # Database schema
│       ├── data.sql                        # Sample data
│       ├── templates/
│       │   └── index.html                  # Main web page
│       └── static/js/
│           └── script.js                   # Client-side JavaScript
└── test/
    └── java/com/example/orders/
        ├── repository/
        │   └── DatabaseAccessTest.java     # Repository tests
        ├── controller/
        │   ├── OrdersControllerTest.java   # REST API tests
        │   └── HomeControllerTest.java     # Web controller tests
        └── properties/
            └── OrdersPropertyTest.java     # Property-based tests
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

4. Access the application:
   - Web UI: http://localhost:8080/
   - REST API: http://localhost:8080/orders
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: (leave empty)

### Running Tests

```bash
mvn test
```

## REST API Endpoints

### GET /orders
Returns all orders sorted by date.

**Response:**
```json
[
  {
    "orderId": 1,
    "items": "GiftCard,Car key",
    "localD": "2022-02-28",
    "localT": "23:59:00",
    "quantity": 5,
    "onHand": false
  }
]
```

### POST /orders
Creates a new order.

**Request Body:**
```json
{
  "items": "Laptop,Mouse",
  "localD": "2023-12-01",
  "localT": "14:30:00",
  "quantity": 2,
  "onHand": true
}
```

### GET /orders/{orderId}
Retrieves a specific order by ID.

### PUT /orders/{orderId}
Updates an order's items field.

**Request Body:**
```json
{
  "items": "Updated items"
}
```

### DELETE /orders/{orderId}
Deletes an order by ID.

## Web Interface Features

### Order List
- View all orders in a table
- Click on item names to expand/collapse details
- Orders sorted by date

### Order Form
- Add new orders with:
  - Items (required text field)
  - Date (date picker)
  - Time (time picker)
  - Quantity (number input)
  - On Hand status (checkbox)

### Actions
- **Delete**: Remove an order from the system
- **Edit**: Load order data into the form for modification

## Database Schema

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

## Configuration

The application uses H2 in-memory database by default. Configuration can be modified in `src/main/resources/application.properties`:

```properties
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
```

## Testing

The project includes comprehensive tests:

- **Unit Tests**: Test individual components (controllers, repository)
- **Integration Tests**: Test database operations
- **Property-Based Tests**: Verify universal properties across random inputs

## Development Notes

- The application uses manual getters/setters instead of Lombok due to Java 25 compatibility
- RestTemplate is configured as a Spring bean for internal REST API calls
- The edit functionality works by deleting the original order and creating a new one

## License

This project is created for educational purposes.
