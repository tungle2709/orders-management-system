package com.example.orders.repository;

import com.example.orders.model.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DatabaseAccessTest {

    @Autowired
    private DatabaseAccess databaseAccess;

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByOrderIdWithNonExistentIdShouldThrowException() {
        // Attempt to find an order that doesn't exist
        assertThrows(IndexOutOfBoundsException.class, () -> {
            databaseAccess.findByOrderId(999L);
        });
    }

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllOrdersWithEmptyDatabaseShouldReturnEmptyList() {
        // Query empty database
        List<Orders> orders = databaseAccess.findAllOrders();
        
        assertNotNull(orders, "Should return a list, not null");
        assertTrue(orders.isEmpty(), "List should be empty");
    }

    @Test
    @Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void saveWithNullItemsShouldSucceedButBeInvalid() {
        // Create order with null items
        Orders order = new Orders();
        order.setLocalD(LocalDate.now());
        order.setLocalT(LocalTime.now());
        order.setQuantity(1);
        order.setOnHand(true);

        // Database allows null, but application should validate
        // This test documents current behavior
        databaseAccess.save(order);
        
        List<Orders> orders = databaseAccess.findAllOrders();
        assertEquals(1, orders.size());
        assertNull(orders.get(0).getItems());
    }
}
