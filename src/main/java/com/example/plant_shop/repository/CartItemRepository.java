package com.example.plant_shop.repository;

import com.example.plant_shop.model.CartItem;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
}
