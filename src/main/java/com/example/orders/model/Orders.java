package com.example.orders.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Orders {
    private Integer orderId;
    private String items;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate localD;
    
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime localT;
    
    private Integer quantity;
    private Boolean onHand;

    // Constructors
    public Orders() {
    }

    public Orders(String items) {
        this.items = items;
    }

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public LocalDate getLocalD() {
        return localD;
    }

    public void setLocalD(LocalDate localD) {
        this.localD = localD;
    }

    public LocalTime getLocalT() {
        return localT;
    }

    public void setLocalT(LocalTime localT) {
        this.localT = localT;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getOnHand() {
        return onHand;
    }

    public void setOnHand(Boolean onHand) {
        this.onHand = onHand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orders orders = (Orders) o;
        return Objects.equals(orderId, orders.orderId) &&
                Objects.equals(items, orders.items) &&
                Objects.equals(localD, orders.localD) &&
                Objects.equals(localT, orders.localT) &&
                Objects.equals(quantity, orders.quantity) &&
                Objects.equals(onHand, orders.onHand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, items, localD, localT, quantity, onHand);
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", items='" + items + '\'' +
                ", localD=" + localD +
                ", localT=" + localT +
                ", quantity=" + quantity +
                ", onHand=" + onHand +
                '}';
    }
}
