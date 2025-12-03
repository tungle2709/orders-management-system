package com.example.orders.controller;

import com.example.orders.model.Orders;
import com.example.orders.repository.DatabaseAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    
    @Autowired
    private DatabaseAccess da;
    
    @GetMapping
    public List<Orders> getOrderCollection() {
        return da.findAllOrders();
    }
    
    @PostMapping(consumes = "application/json")
    public String postOrder(@RequestBody Orders orders) {
        da.save(orders);
        return "http://localhost:8080/orders/";
    }
    
    @GetMapping(value = "/{orderId}")
    public Orders getIndividualOrder(@PathVariable Long orderId) {
        return da.findByOrderId(orderId);
    }
    
    @PutMapping(value = "/{orderId}")
    public String updateOrderIndividual(@PathVariable Long orderId, @RequestBody Orders orders) {
        da.updateIndividualOrder(orderId, orders);
        return "Updated";
    }
    
    @DeleteMapping(value = "/{orderId}")
    public String deleteOrderById(@PathVariable Long orderId) {
        da.deleteById(orderId);
        return "Order has been deleted";
    }
}
