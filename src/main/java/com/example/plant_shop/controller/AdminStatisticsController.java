package com.example.plant_shop.controller;

import com.example.plant_shop.dto.OrderStatisticsDTO;
import com.example.plant_shop.service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService statisticsService;

    @GetMapping
    public String showStatistics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 Model model) {
        if (date == null) date = LocalDate.now();

        List<OrderStatisticsDTO> stats = statisticsService.getOrderStatisticsByDate(date);
        long totalOrders = statisticsService.getTotalOrdersCount(date);
        double totalRevenue = statisticsService.getTotalRevenue(date);

        model.addAttribute("stats", stats);
        model.addAttribute("date", date);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin/statistics";
    }
}

