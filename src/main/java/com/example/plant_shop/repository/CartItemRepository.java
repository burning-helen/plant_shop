package com.example.plant_shop.repository;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.CartItem;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);

    List<CartItem> findByIdInAndUser(List<Long> itemIds, User user);

    List<CartItem> findByUserAndIdIn(User user, List<Long> selectedItemIds);

    void deleteAllByCart(Cart cart);
}
