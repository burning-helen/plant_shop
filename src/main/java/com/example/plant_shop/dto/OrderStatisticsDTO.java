package com.example.plant_shop.dto;

/**
 * DTO (Data Transfer Object) для представления статистики по заказам растений.
 * Содержит информацию о названии растения, общем количестве проданных единиц и общей выручке.
 */
public class OrderStatisticsDTO {

    /** Название растения */
    private String plantName;

    /** Общее количество проданных единиц растения */
    private Long totalQuantity;

    /** Общая выручка, полученная от продажи данного растения */
    private Double totalRevenue;

    /**
     * Конструктор для инициализации всех полей.
     *
     * @param plantName Название растения
     * @param totalQuantity Общее количество проданных единиц
     * @param totalRevenue Общая выручка от продаж
     */
    public OrderStatisticsDTO(String plantName, Long totalQuantity, Double totalRevenue) {
        this.plantName = plantName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    /**
     * @return Название растения
     */
    public String getPlantName() {
        return plantName;
    }

    /**
     * @return Общее количество проданных единиц
     */
    public Long getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * @return Общая выручка от продаж
     */
    public Double getTotalRevenue() {
        return totalRevenue;
    }
}
