package com.example.orders.repository;

import com.example.orders.model.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DatabaseAccess {
    
    @Autowired
    protected NamedParameterJdbcTemplate jdbc;
    
    // Used to find all stored orders and display in the list
    public List<Orders> findAllOrders() {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM orders ORDER BY localD";
        return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<>(Orders.class));
    }
    
    // Saves user entered data in database
    public void save(Orders orders) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO orders(items, localD, localT, quantity, onHand) " +
                      "VALUES(:items, :localD, :localT, :quantity, :onHand)";
        
        namedParameters.addValue("items", orders.getItems());
        namedParameters.addValue("localD", orders.getLocalD());
        namedParameters.addValue("localT", orders.getLocalT());
        namedParameters.addValue("quantity", orders.getQuantity());
        namedParameters.addValue("onHand", orders.getOnHand());
        
        jdbc.update(query, namedParameters, generatedKeyHolder);
    }
    
    // Retrieve data based on orderId
    public Orders findByOrderId(Long orderId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM orders WHERE orderId = :orderId";
        namedParameters.addValue("orderId", orderId);
        return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<>(Orders.class)).get(0);
    }
    
    // Deletes user order based on OrderId
    public void deleteById(Long orderId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "DELETE FROM orders WHERE orderId = :orderId";
        namedParameters.addValue("orderId", orderId);
        jdbc.update(query, namedParameters);
    }
    
    // Updates individual data
    public void updateIndividualOrder(Long orderId, Orders orders) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "UPDATE orders SET items=:items WHERE orderId = :orderId";
        namedParameters.addValue("orderId", orderId).addValue("items", orders.getItems());
        jdbc.update(query, namedParameters);
    }
}
