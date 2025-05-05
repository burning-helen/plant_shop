package com.example.plant_shop.service;

import com.example.plant_shop.dto.OrderStatisticsDTO;
import com.example.plant_shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminStatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderStatisticsDTO> getOrderStatisticsByDate(LocalDate date) {
        return orderRepository.findOrderStatisticsByDate(date);
    }

    public long getTotalOrdersCount(LocalDate date) {
        return orderRepository.countByOrderDateBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    public double getTotalRevenue(LocalDate date) {
        Double total = orderRepository.getTotalRevenueByDate(date);
        return total != null ? total : 0.0;
    }
}

