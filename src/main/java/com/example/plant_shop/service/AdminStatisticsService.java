package com.example.plant_shop.service;

import com.example.plant_shop.dto.OrderStatisticsDTO;
import com.example.plant_shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с данными статистики заказов администратора.
 * <p>
 * Этот сервис предоставляет методы для получения статистики заказов по дате, общего количества заказов и общей выручки.
 * </p>
 */
@Service
public class AdminStatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Получает статистику по заказам за указанную дату.
     * Статистика включает название растения, общее количество и общую выручку.
     *
     * @param date дата для статистики
     * @return список статистики заказов на указанную дату
     */
    public List<OrderStatisticsDTO> getOrderStatisticsByDate(LocalDate date) {
        return orderRepository.findOrderStatisticsByDate(date);
    }

    /**
     * Получает общее количество заказов, сделанных в указанный день.
     *
     * @param date дата для подсчета заказов
     * @return общее количество заказов за указанную дату
     */
    public long getTotalOrdersCount(LocalDate date) {
        return orderRepository.countByOrderDateBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    /**
     * Получает общую выручку, полученную за указанный день.
     *
     * @param date дата для подсчета выручки
     * @return общая выручка за указанную дату
     */
    public double getTotalRevenue(LocalDate date) {
        Double total = orderRepository.getTotalRevenueByDate(date);
        return total != null ? total : 0.0;
    }
}
