package com.example.plant_shop.repository;

import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findWithItemsByUser(@Param("user") User user);

}
