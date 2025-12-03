# Implementation Plan - Orders Management System

- [x] 1. Set up Spring Boot project structure and dependencies
  - Create Maven/Gradle project with Spring Boot
  - Add dependencies: spring-boot-starter-web, spring-boot-starter-jdbc, spring-boot-starter-thymeleaf, h2database, lombok, spring-boot-starter-test, jqwik
  - Configure Lombok annotation processing
  - Create package structure: model, repository, controller
  - _Requirements: 7.1, 7.4_

- [x] 2. Configure database and create schema
  - Create application.properties with H2 configuration
  - Create schema.sql with orders table definition
  - Create data.sql with sample order data
  - Verify H2 console accessibility
  - _Requirements: 6.1, 6.2, 7.1, 7.2, 7.3_

- [x] 3. Implement Orders model class
  - Create Orders.java POJO with all fields
  - Add Lombok annotations (@Data, @NoArgsConstructor, @RequiredArgsConstructor)
  - Add @NonNull annotation on items field
  - Add @DateTimeFormat annotations for date and time fields
  - _Requirements: 2.3, 2.5_

- [ ] 4. Implement DatabaseAccess repository
- [x] 4.1 Create DatabaseAccess class with JDBC template
  - Add @Repository annotation
  - Inject NamedParameterJdbcTemplate
  - _Requirements: 1.1, 2.2, 3.1, 4.3, 5.1_

- [x] 4.2 Implement findAllOrders method
  - Write SQL query with ORDER BY localD
  - Use BeanPropertyRowMapper for result mapping
  - _Requirements: 1.1, 5.1_

- [x] 4.3 Write property test for findAllOrders sorting
  - **Property 1: Orders sorted by date**
  - **Validates: Requirements 1.1**

- [x] 4.4 Implement save method
  - Write INSERT query with named parameters
  - Use KeyHolder for auto-generated ID
  - _Requirements: 2.2, 2.5_

- [x] 4.5 Write property test for save-retrieve round trip
  - **Property 3: Save-retrieve round trip**
  - **Validates: Requirements 2.2, 2.4**

- [x] 4.6 Write property test for unique auto-increment IDs
  - **Property 5: Unique auto-increment IDs**
  - **Validates: Requirements 2.5**

- [x] 4.7 Implement findByOrderId method
  - Write SELECT query with WHERE clause
  - Return single Orders object
  - _Requirements: 1.3, 5.3_

- [x] 4.8 Write property test for complete order information retrieval
  - **Property 2: Complete order information retrieval**
  - **Validates: Requirements 1.3**

- [x] 4.9 Implement deleteById method
  - Write DELETE query with orderId parameter
  - _Requirements: 3.1, 5.5_

- [x] 4.10 Write property test for delete removes order
  - **Property 6: Delete removes order**
  - **Validates: Requirements 3.1, 3.3**

- [x] 4.11 Implement updateIndividualOrder method
  - Write UPDATE query for items field
  - Use named parameters for orderId and items
  - _Requirements: 4.3, 5.4_

- [x] 4.12 Write property test for update modifies order data
  - **Property 7: Update modifies order data**
  - **Validates: Requirements 4.3**

- [x] 4.13 Write unit tests for DatabaseAccess edge cases
  - Test findByOrderId with non-existent ID
  - Test findAllOrders with empty database
  - Test save with null items (should fail)
  - _Requirements: 2.3_

- [x] 4.14 Write property test for empty items validation
  - **Property 4: Empty items validation**
  - **Validates: Requirements 2.3**

- [ ] 5. Implement OrdersController REST API
- [x] 5.1 Create OrdersController class
  - Add @RestController and @RequestMapping("/orders")
  - Inject DatabaseAccess dependency
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 5.2 Implement GET /orders endpoint
  - Return List<Orders> from findAllOrders
  - _Requirements: 5.1_

- [x] 5.3 Implement POST /orders endpoint
  - Accept @RequestBody Orders object
  - Call save method
  - Return URL string
  - _Requirements: 5.2_

- [x] 5.4 Implement GET /orders/{orderId} endpoint
  - Accept @PathVariable orderId
  - Return Orders object from findByOrderId
  - _Requirements: 5.3_

- [x] 5.5 Implement PUT /orders/{orderId} endpoint
  - Accept @PathVariable orderId and @RequestBody Orders
  - Call updateIndividualOrder
  - Return "Updated" message
  - _Requirements: 5.4_

- [x] 5.6 Implement DELETE /orders/{orderId} endpoint
  - Accept @PathVariable orderId
  - Call deleteById
  - Return deletion confirmation message
  - _Requirements: 5.5_

- [x] 5.7 Write unit tests for OrdersController endpoints
  - Test all REST endpoints with MockMvc
  - Verify HTTP status codes
  - Test JSON serialization/deserialization
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 6. Configure RestTemplate bean
  - Add @Bean method in main application class
  - Create and return RestTemplate instance
  - _Requirements: 1.1, 2.2, 3.1, 4.3_

- [ ] 7. Implement HomeController for web UI
- [x] 7.1 Create HomeController class
  - Add @Controller annotation
  - Inject RestTemplate dependency
  - _Requirements: 1.1, 2.2, 3.1, 4.3_

- [x] 7.2 Implement GET / endpoint
  - Fetch orders via RestTemplate from /orders
  - Add ordersList and empty Orders to model
  - Return "index" view
  - _Requirements: 1.1_

- [x] 7.3 Implement POST /insertOrders endpoint
  - Accept @ModelAttribute Orders object
  - POST to REST API via RestTemplate
  - Return "index" view
  - _Requirements: 2.2_

- [x] 7.4 Implement GET /insertOrders endpoint
  - Redirect to home page
  - _Requirements: 2.4_

- [x] 7.5 Implement GET /deleteOrders/{orderId} endpoint
  - Call REST API delete via RestTemplate
  - Redirect to home page
  - _Requirements: 3.1_

- [x] 7.6 Implement GET /editOrders/{orderId} endpoint
  - Fetch order via RestTemplate
  - Delete original order via RestTemplate
  - Add order to model for form population
  - Return "index" view
  - _Requirements: 4.1, 4.2, 4.3_

- [x] 7.7 Write unit tests for HomeController
  - Test model attributes are set correctly
  - Test redirects work properly
  - Test RestTemplate interactions
  - _Requirements: 1.1, 2.2, 3.1, 4.3_

- [ ] 8. Create index.html view
- [x] 8.1 Create HTML structure with Thymeleaf
  - Add Thymeleaf namespace
  - Create basic HTML structure with head and body
  - _Requirements: 1.2, 2.1_

- [x] 8.2 Implement order list table
  - Create table with headers for Items and Date
  - Add th:each loop for ordersList
  - Add clickable item names with th:onclick
  - Add div for dynamic detail display with th:id
  - Display date with th:text
  - _Requirements: 1.2, 1.3_

- [x] 8.3 Add Delete and Edit columns to table
  - Add "Delete Order" header and delete links
  - Add "Edit Order" header and edit links
  - Use th:href with orderId for both
  - _Requirements: 3.1, 4.1_

- [x] 8.4 Implement order form
  - Create form with th:action and th:object
  - Add hidden input for orderId
  - Add text input for items (required)
  - Add date input for localD
  - Add time input for localT
  - Add number input for quantity
  - Add checkbox for onHand
  - Add submit button
  - _Requirements: 2.1, 2.3_

- [x] 8.5 Link JavaScript file
  - Add script tag with th:src for script.js
  - _Requirements: 8.1, 8.2_

- [ ] 9. Create script.js for dynamic order details
- [x] 9.1 Implement getOrders function
  - Check if detail div is empty
  - Fetch order data from REST API using orderId
  - Parse JSON response
  - Build HTML string with order details
  - Update div innerHTML with details
  - Handle toggle to clear details
  - _Requirements: 8.1, 8.2, 8.3, 8.4_

- [x] 10. Final integration and testing checkpoint
  - Ensure all tests pass
  - Verify application starts without errors
  - Test H2 console access at /h2-console
  - Test REST API endpoints with browser or Postman
  - Test web UI form submission, delete, and edit
  - Test JavaScript order detail expansion
  - Ask the user if questions arise
  - _Requirements: All_
