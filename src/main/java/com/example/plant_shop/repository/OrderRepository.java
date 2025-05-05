package com.example.plant_shop.repository;

import com.example.plant_shop.dto.OrderStatisticsDTO;
import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findWithItemsByUser(@Param("user") User user);

    @Query("SELECT new com.example.plant_shop.dto.OrderStatisticsDTO(oi.plant.name, SUM(oi.quantity), SUM(oi.quantity * oi.price)) " +
            "FROM Order o JOIN o.items oi " +
            "WHERE DATE(o.orderDate) = :date " +
            "GROUP BY oi.plant.name")
    List<OrderStatisticsDTO> findOrderStatisticsByDate(@Param("date") LocalDate date);

    long countByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE DATE(o.orderDate) = :date")
    Double getTotalRevenueByDate(@Param("date") LocalDate date);

}
