package com.example.orders.controller;

import com.example.orders.model.Orders;
import com.example.orders.repository.DatabaseAccess;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DatabaseAccess databaseAccess;

    @Test
    void getOrderCollectionShouldReturnAllOrders() throws Exception {
        // Arrange
        Orders order1 = new Orders("Item1");
        order1.setOrderId(1);
        order1.setLocalD(LocalDate.of(2023, 1, 1));
        order1.setLocalT(LocalTime.of(10, 0));
        order1.setQuantity(5);
        order1.setOnHand(true);

        Orders order2 = new Orders("Item2");
        order2.setOrderId(2);
        order2.setLocalD(LocalDate.of(2023, 1, 2));
        order2.setLocalT(LocalTime.of(11, 0));
        order2.setQuantity(3);
        order2.setOnHand(false);

        List<Orders> ordersList = Arrays.asList(order1, order2);
        when(databaseAccess.findAllOrders()).thenReturn(ordersList);

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].items").value("Item1"))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].items").value("Item2"));

        verify(databaseAccess, times(1)).findAllOrders();
    }

    @Test
    void postOrderShouldCreateNewOrder() throws Exception {
        // Arrange
        Orders newOrder = new Orders("NewItem");
        newOrder.setLocalD(LocalDate.of(2023, 1, 3));
        newOrder.setLocalT(LocalTime.of(12, 0));
        newOrder.setQuantity(10);
        newOrder.setOnHand(true);

        doNothing().when(databaseAccess).save(any(Orders.class));

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost:8080/orders/"));

        verify(databaseAccess, times(1)).save(any(Orders.class));
    }

    @Test
    void getIndividualOrderShouldReturnSpecificOrder() throws Exception {
        // Arrange
        Orders order = new Orders("SpecificItem");
        order.setOrderId(1);
        order.setLocalD(LocalDate.of(2023, 1, 1));
        order.setLocalT(LocalTime.of(10, 0));
        order.setQuantity(5);
        order.setOnHand(true);

        when(databaseAccess.findByOrderId(1L)).thenReturn(order);

        // Act & Assert
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.items").value("SpecificItem"));

        verify(databaseAccess, times(1)).findByOrderId(1L);
    }

    @Test
    void updateOrderIndividualShouldUpdateOrder() throws Exception {
        // Arrange
        Orders updateData = new Orders("UpdatedItem");

        doNothing().when(databaseAccess).updateIndividualOrder(eq(1L), any(Orders.class));

        // Act & Assert
        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));

        verify(databaseAccess, times(1)).updateIndividualOrder(eq(1L), any(Orders.class));
    }

    @Test
    void deleteOrderByIdShouldDeleteOrder() throws Exception {
        // Arrange
        doNothing().when(databaseAccess).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order has been deleted"));

        verify(databaseAccess, times(1)).deleteById(1L);
    }
}
