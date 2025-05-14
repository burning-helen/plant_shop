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

/**
 * Репозиторий для работы с объектами {@link Order}.
 * <p>
 * Этот интерфейс предоставляет методы для выполнения различных операций с сущностями {@link Order} в базе данных.
 * Он наследует стандартные операции CRUD из {@link JpaRepository}, а также включает дополнительные запросы для получения статистики и информации о заказах.
 * </p>
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Находит все заказы, связанные с указанным пользователем.
     *
     * @param user пользователь, чьи заказы нужно найти
     * @return список заказов пользователя
     */
    List<Order> findByUser(User user);

    /**
     * Находит все заказы в порядке убывания даты.
     *
     * @return список всех заказов, отсортированных по дате
     */
    List<Order> findAllByOrderByOrderDateDesc();

    /**
     * Находит все заказы пользователя с полными данными о связанных элементах (предметах заказа и растениях).
     *
     * @param user пользователь, чьи заказы нужно найти
     * @return список заказов с полными данными
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.plant WHERE o.user = :user")
    List<Order> findWithItemsByUser(@Param("user") User user);

    /**
     * Получает статистику по заказам на указанную дату.
     * Статистика включает название растения, общее количество и общую выручку.
     *
     * @param date дата для получения статистики
     * @return список статистики по заказам
     */
    @Query("SELECT new com.example.plant_shop.dto.OrderStatisticsDTO(oi.plant.name, SUM(oi.quantity), SUM(oi.quantity * oi.price)) " +
            "FROM Order o JOIN o.items oi " +
            "WHERE DATE(o.orderDate) = :date " +
            "GROUP BY oi.plant.name")
    List<OrderStatisticsDTO> findOrderStatisticsByDate(@Param("date") LocalDate date);

    /**
     * Подсчитывает количество заказов в определённом временном интервале.
     *
     * @param start начало временного интервала
     * @param end конец временного интервала
     * @return количество заказов
     */
    long countByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Получает общую выручку по заказам на указанную дату.
     *
     * @param date дата для получения выручки
     * @return общая выручка
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE DATE(o.orderDate) = :date")
    Double getTotalRevenueByDate(@Param("date") LocalDate date);

}
