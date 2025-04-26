package com.example.plant_shop.repository;

import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
