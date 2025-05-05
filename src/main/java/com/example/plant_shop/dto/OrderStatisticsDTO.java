package com.example.plant_shop.dto;

public class OrderStatisticsDTO {
    private String plantName;
    private Long totalQuantity;
    private Double totalRevenue;

    public OrderStatisticsDTO(String plantName, Long totalQuantity, Double totalRevenue) {
        this.plantName = plantName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public String getPlantName() { return plantName; }
    public Long getTotalQuantity() { return totalQuantity; }
    public Double getTotalRevenue() { return totalRevenue; }
}
