package com.example.plant_shop.repository;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

