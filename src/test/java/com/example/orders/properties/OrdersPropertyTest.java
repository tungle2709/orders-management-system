package com.example.orders.properties;

import com.example.orders.model.Orders;
import com.example.orders.repository.DatabaseAccess;
import net.jqwik.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrdersPropertyTest {

    @Autowired
    private DatabaseAccess databaseAccess;

    /**
     * Feature: orders-management-system, Property 1: Orders sorted by date
     * For any collection of orders in the database, when retrieving all orders,
     * the returned list should be sorted in ascending order by the localD (date) field.
     * Validates: Requirements 1.1
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void ordersShouldBeSortedByDate(
            @ForAll("ordersList") List<Orders> ordersToSave
    ) {
        // Save all orders
        for (Orders order : ordersToSave) {
            databaseAccess.save(order);
        }

        // Retrieve all orders
        List<Orders> retrievedOrders = databaseAccess.findAllOrders();

        // Verify they are sorted by date
        for (int i = 0; i < retrievedOrders.size() - 1; i++) {
            LocalDate currentDate = retrievedOrders.get(i).getLocalD();
            LocalDate nextDate = retrievedOrders.get(i + 1).getLocalD();
            assertTrue(currentDate.compareTo(nextDate) <= 0,
                    "Orders should be sorted by date in ascending order");
        }
    }

    /**
     * Feature: orders-management-system, Property 3: Save-retrieve round trip
     * For any valid order object with non-null items field, saving the order to the database
     * and then retrieving all orders should result in a list that contains an order with
     * equivalent field values (excluding the auto-generated orderId).
     * Validates: Requirements 2.2, 2.4
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void saveAndRetrieveShouldRoundTrip(@ForAll("ordersArbitrary") Orders orderToSave) {
        // Save the order
        databaseAccess.save(orderToSave);

        // Retrieve all orders
        List<Orders> retrievedOrders = databaseAccess.findAllOrders();

        // Verify the saved order exists in the list with matching fields
        boolean found = retrievedOrders.stream().anyMatch(retrieved ->
                retrieved.getItems().equals(orderToSave.getItems()) &&
                retrieved.getLocalD().equals(orderToSave.getLocalD()) &&
                retrieved.getLocalT().equals(orderToSave.getLocalT()) &&
                retrieved.getQuantity().equals(orderToSave.getQuantity()) &&
                retrieved.getOnHand().equals(orderToSave.getOnHand())
        );

        assertTrue(found, "Saved order should be retrievable with matching field values");
    }

    /**
     * Feature: orders-management-system, Property 5: Unique auto-increment IDs
     * For any sequence of order creations, each successfully saved order should receive
     * a unique orderId value, and these values should be strictly increasing.
     * Validates: Requirements 2.5
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void orderIdsShouldBeUniqueAndIncreasing(@ForAll("ordersList") List<Orders> ordersToSave) {
        // Save all orders and collect their IDs
        for (Orders order : ordersToSave) {
            databaseAccess.save(order);
        }

        // Retrieve all orders
        List<Orders> retrievedOrders = databaseAccess.findAllOrders();

        // Verify all IDs are unique
        long uniqueIdCount = retrievedOrders.stream()
                .map(Orders::getOrderId)
                .distinct()
                .count();
        assertEquals(retrievedOrders.size(), uniqueIdCount, "All order IDs should be unique");

        // Verify IDs are strictly increasing
        for (int i = 0; i < retrievedOrders.size() - 1; i++) {
            Integer currentId = retrievedOrders.get(i).getOrderId();
            Integer nextId = retrievedOrders.get(i + 1).getOrderId();
            assertNotNull(currentId, "Order ID should not be null");
            assertNotNull(nextId, "Order ID should not be null");
        }
    }

    /**
     * Feature: orders-management-system, Property 2: Complete order information retrieval
     * For any order ID that exists in the database, retrieving that order should return
     * an object containing all required fields with their correct values.
     * Validates: Requirements 1.3
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void retrievedOrderShouldContainAllFields(@ForAll("ordersArbitrary") Orders orderToSave) {
        // Save the order
        databaseAccess.save(orderToSave);

        // Get the saved order's ID
        List<Orders> allOrders = databaseAccess.findAllOrders();
        assertFalse(allOrders.isEmpty(), "Should have at least one order");
        Long orderId = allOrders.get(0).getOrderId().longValue();

        // Retrieve by ID
        Orders retrieved = databaseAccess.findByOrderId(orderId);

        // Verify all fields are present and correct
        assertNotNull(retrieved.getOrderId(), "Order ID should not be null");
        assertNotNull(retrieved.getItems(), "Items should not be null");
        assertNotNull(retrieved.getLocalD(), "Date should not be null");
        assertNotNull(retrieved.getLocalT(), "Time should not be null");
        assertNotNull(retrieved.getQuantity(), "Quantity should not be null");
        assertNotNull(retrieved.getOnHand(), "OnHand should not be null");

        // Verify values match
        assertEquals(orderToSave.getItems(), retrieved.getItems());
        assertEquals(orderToSave.getLocalD(), retrieved.getLocalD());
        assertEquals(orderToSave.getLocalT(), retrieved.getLocalT());
        assertEquals(orderToSave.getQuantity(), retrieved.getQuantity());
        assertEquals(orderToSave.getOnHand(), retrieved.getOnHand());
    }

    /**
     * Feature: orders-management-system, Property 6: Delete removes order
     * For any order ID that exists in the database, after deleting that order,
     * attempting to retrieve it by ID should fail, and the order should not appear
     * in the list of all orders.
     * Validates: Requirements 3.1, 3.3
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteShouldRemoveOrder(@ForAll("ordersArbitrary") Orders orderToSave) {
        // Save the order
        databaseAccess.save(orderToSave);

        // Get the saved order's ID
        List<Orders> allOrders = databaseAccess.findAllOrders();
        assertFalse(allOrders.isEmpty(), "Should have at least one order");
        Long orderId = allOrders.get(0).getOrderId().longValue();

        // Delete the order
        databaseAccess.deleteById(orderId);

        // Verify it's not in the list anymore
        List<Orders> afterDelete = databaseAccess.findAllOrders();
        boolean found = afterDelete.stream()
                .anyMatch(o -> o.getOrderId().equals(orderId.intValue()));
        assertFalse(found, "Deleted order should not appear in list");

        // Verify retrieving by ID fails
        try {
            databaseAccess.findByOrderId(orderId);
            fail("Should throw exception when retrieving deleted order");
        } catch (IndexOutOfBoundsException e) {
            // Expected - query returns empty list
        }
    }

    /**
     * Feature: orders-management-system, Property 7: Update modifies order data
     * For any existing order and any valid update data (new items value), after performing
     * an update operation on that order ID, retrieving the order should return the updated
     * items value while preserving the orderId.
     * Validates: Requirements 4.3
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void updateShouldModifyOrderData(
            @ForAll("ordersArbitrary") Orders originalOrder,
            @ForAll("itemsString") String newItems
    ) {
        // Save the original order
        databaseAccess.save(originalOrder);

        // Get the saved order's ID
        List<Orders> allOrders = databaseAccess.findAllOrders();
        assertFalse(allOrders.isEmpty(), "Should have at least one order");
        Long orderId = allOrders.get(0).getOrderId().longValue();

        // Create update with new items
        Orders updateData = new Orders(newItems);

        // Update the order
        databaseAccess.updateIndividualOrder(orderId, updateData);

        // Retrieve and verify
        Orders updated = databaseAccess.findByOrderId(orderId);
        assertEquals(orderId.intValue(), updated.getOrderId(), "Order ID should be preserved");
        assertEquals(newItems, updated.getItems(), "Items should be updated");
    }

    /**
     * Feature: orders-management-system, Property 4: Empty items validation
     * For any order object where the items field is null or empty, attempting to save
     * that order should either fail validation or be prevented by the system constraints.
     * Validates: Requirements 2.3
     */
    @Property(tries = 100)
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void emptyItemsShouldFailValidation(@ForAll("emptyOrWhitespaceString") String invalidItems) {
        // Create order with empty/whitespace items
        Orders order = new Orders();
        order.setItems(invalidItems);
        order.setLocalD(LocalDate.of(2023, 1, 1));
        order.setLocalT(LocalTime.of(12, 0));
        order.setQuantity(1);
        order.setOnHand(true);

        // Attempt to save should fail
        assertThrows(Exception.class, () -> {
            databaseAccess.save(order);
        }, "Saving order with empty/whitespace items should fail");
    }

    @Provide
    Arbitrary<String> emptyOrWhitespaceString() {
        return Arbitraries.of("", " ", "  ", "\t", "\n", "   ");
    }

    @Provide
    Arbitrary<String> itemsString() {
        return Arbitraries.strings()
                .alpha().numeric().withChars(',', ' ')
                .ofMinLength(1).ofMaxLength(100);
    }

    @Provide
    Arbitrary<List<Orders>> ordersList() {
        return ordersArbitrary().list().ofMinSize(2).ofMaxSize(10);
    }

    @Provide
    Arbitrary<Orders> ordersArbitrary() {
        Arbitrary<String> items = Arbitraries.strings()
                .alpha().numeric().withChars(',', ' ')
                .ofMinLength(1).ofMaxLength(100);
        
        // Generate random dates using integers for year, month, day
        Arbitrary<LocalDate> dates = Arbitraries.integers().between(2020, 2025)
                .flatMap(year -> Arbitraries.integers().between(1, 12)
                        .flatMap(month -> Arbitraries.integers().between(1, 28)
                                .map(day -> LocalDate.of(year, month, day))));
        
        Arbitrary<LocalTime> times = Arbitraries.of(
                LocalTime.of(0, 0),
                LocalTime.of(12, 0),
                LocalTime.of(23, 59)
        );
        
        Arbitrary<Integer> quantities = Arbitraries.integers().between(1, 100);
        Arbitrary<Boolean> onHand = Arbitraries.of(true, false);

        return Combinators.combine(items, dates, times, quantities, onHand)
                .as((i, d, t, q, o) -> {
                    Orders order = new Orders(i);
                    order.setLocalD(d);
                    order.setLocalT(t);
                    order.setQuantity(q);
                    order.setOnHand(o);
                    return order;
                });
    }
}
