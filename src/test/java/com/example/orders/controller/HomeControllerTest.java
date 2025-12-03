package com.example.orders.controller;

import com.example.orders.model.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void indexShouldSetModelAttributesAndReturnIndexView() throws Exception {
        // Arrange
        Orders order1 = new Orders("Item1");
        order1.setOrderId(1);
        order1.setLocalD(LocalDate.of(2023, 1, 1));
        order1.setLocalT(LocalTime.of(10, 0));
        order1.setQuantity(5);
        order1.setOnHand(true);

        Orders[] ordersArray = {order1};
        ResponseEntity<Orders[]> responseEntity = ResponseEntity.ok(ordersArray);

        when(restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class))
                .thenReturn(responseEntity);

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("ordersList"))
                .andExpect(model().attributeExists("orders"));

        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/orders", Orders[].class);
    }

    @Test
    void insertOrdersShouldPostToRestApiAndReturnIndexView() throws Exception {
        // Arrange
        ResponseEntity<String> postResponse = ResponseEntity.ok("http://localhost:8080/orders/");
        when(restTemplate.postForEntity(eq("http://localhost:8080/orders"), any(Orders.class), eq(String.class)))
                .thenReturn(postResponse);

        // Act & Assert
        mockMvc.perform(post("/insertOrders")
                        .param("items", "TestItem")
                        .param("localD", "2023-01-01")
                        .param("localT", "10:00")
                        .param("quantity", "5")
                        .param("onHand", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("orders"));

        verify(restTemplate, times(1)).postForEntity(eq("http://localhost:8080/orders"), any(Orders.class), eq(String.class));
    }

    @Test
    void insertOrdersGetShouldRedirectToHome() throws Exception {
        // Arrange
        Orders[] ordersArray = {};
        ResponseEntity<Orders[]> responseEntity = ResponseEntity.ok(ordersArray);

        when(restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class))
                .thenReturn(responseEntity);

        // Act & Assert
        mockMvc.perform(get("/insertOrders"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/orders", Orders[].class);
    }

    @Test
    void deleteDataShouldDeleteOrderAndRedirect() throws Exception {
        // Arrange
        doNothing().when(restTemplate).delete("http://localhost:8080/orders/1");

        Orders[] ordersArray = {};
        ResponseEntity<Orders[]> responseEntity = ResponseEntity.ok(ordersArray);
        when(restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class))
                .thenReturn(responseEntity);

        // Act & Assert
        mockMvc.perform(get("/deleteOrders/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(restTemplate, times(1)).delete("http://localhost:8080/orders/1");
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/orders", Orders[].class);
    }

    @Test
    void editOrderShouldFetchDeleteAndPopulateForm() throws Exception {
        // Arrange
        Orders order = new Orders("EditItem");
        order.setOrderId(1);
        order.setLocalD(LocalDate.of(2023, 1, 1));
        order.setLocalT(LocalTime.of(10, 0));
        order.setQuantity(5);
        order.setOnHand(true);

        ResponseEntity<Orders> orderResponse = ResponseEntity.ok(order);
        when(restTemplate.getForEntity("http://localhost:8080/orders/1", Orders.class))
                .thenReturn(orderResponse);

        doNothing().when(restTemplate).delete("http://localhost:8080/orders/1");

        Orders[] ordersArray = {};
        ResponseEntity<Orders[]> responseEntity = ResponseEntity.ok(ordersArray);
        when(restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class))
                .thenReturn(responseEntity);

        // Act & Assert
        mockMvc.perform(get("/editOrders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("ordersList"));

        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/orders/1", Orders.class);
        verify(restTemplate, times(1)).delete("http://localhost:8080/orders/1");
        verify(restTemplate, times(1)).getForEntity("http://localhost:8080/orders", Orders[].class);
    }
}
