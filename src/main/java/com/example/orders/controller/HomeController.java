package com.example.orders.controller;

import com.example.orders.model.Orders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String index(Model model) {
        ResponseEntity<Orders[]> responseEntity =
                restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class);
        model.addAttribute("ordersList", responseEntity.getBody());
        model.addAttribute("orders", new Orders());
        return "index";
    }

    @PostMapping("/insertOrders")
    public String insertOrders(Model model, @ModelAttribute Orders orders) {
        restTemplate.postForEntity("http://localhost:8080/orders", orders, String.class);
        return "redirect:/";
    }

    @GetMapping("/insertOrders")
    public String insertOrdersGet(Model model) {
        model.addAttribute("ordersList",
                restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class).getBody());
        return "redirect:/";
    }

    @GetMapping("/deleteOrders/{orderId}")
    public String deleteData(Model model, @PathVariable Long orderId) {
        restTemplate.delete("http://localhost:8080/orders/" + orderId);
        model.addAttribute("ordersList",
                restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class).getBody());
        return "redirect:/";
    }

    @GetMapping("/editOrders/{orderId}")
    public String editOrder(Model model, @PathVariable Long orderId) {
        Orders orders = restTemplate.getForEntity("http://localhost:8080/orders/" + orderId,
                Orders.class).getBody();
        restTemplate.delete("http://localhost:8080/orders/" + orderId);
        model.addAttribute("ordersList",
                restTemplate.getForEntity("http://localhost:8080/orders", Orders[].class).getBody());
        model.addAttribute("orders", orders);
        return "index";
    }
}
