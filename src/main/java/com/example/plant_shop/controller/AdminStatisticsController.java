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

/**
 * Контроллер для отображения статистики заказов для администратора.
 * <p>
 * Этот контроллер предоставляет данные о заказах за определенную дату, включая количество заказов и общую выручку.
 * </p>
 */
@Controller
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService statisticsService;

    /**
     * Отображает статистику заказов для указанной даты.
     * <p>
     * Этот метод отображает статистику заказов, включая количество заказов и общую выручку на указанную дату.
     * Если дата не указана, используется текущая дата.
     * </p>
     *
     * @param date дата для получения статистики (если не указана, используется текущая дата)
     * @param model модель для передачи данных в шаблон
     * @return имя шаблона для отображения статистики заказов
     */
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
