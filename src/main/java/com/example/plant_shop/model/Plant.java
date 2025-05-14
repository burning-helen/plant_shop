package com.example.plant_shop.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

import java.util.List;

/**
 * Модель растения в магазине.
 * Содержит информацию о названии, описании, цене, количестве на складе,
 * изображении, категории, типе растения и статусе активности.
 */
@Entity
@Table(name = "plants")
public class Plant {

    /**
     * Конструктор без аргументов.
     */
    public Plant() {}

    /**
     * Конструктор со всеми основными параметрами.
     *
     * @param id идентификатор растения
     * @param name название растения
     * @param description описание растения
     * @param price цена растения
     * @param stockQuantity количество на складе
     * @param imageUrl ссылка на изображение растения
     * @param parentCategory родительская категория
     * @param subcategory подкатегория
     * @param plantType тип растения (семена, рассада и т.д.)
     */
    public Plant(Long id, String name, String description, double price, int stockQuantity,
                 String imageUrl, Category parentCategory, Category subcategory, PlantType plantType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.parentCategory = parentCategory;
        this.subcategory = subcategory;
        this.plantType = plantType;
    }

    /**
     * Уникальный идентификатор растения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "plant_seq", sequenceName = "plant_sequence", allocationSize = 1)
    @Column(name= "id")
    private Long id;

    /**
     * Тип растения (семена, рассада и т.д.).
     */
    @Enumerated(EnumType.STRING)
    private PlantType plantType;

    /**
     * Список заказанных экземпляров этого растения.
     */
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    /**
     * Флаг активности (true — доступно, false — скрыто).
     */
    @Column(name = "active")
    private Boolean active = true;

    /**
     * Название растения.
     */
    @Column(name = "name")
    private String name;

    /**
     * Описание растения.
     */
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * Цена растения.
     */
    @Column(name = "price")
    private double price;

    /**
     * Количество экземпляров на складе.
     */
    @Column(name = "stockQuantity")
    private int stockQuantity;

    /**
     * Ссылка на изображение растения.
     */
    @Column(name = "imageUrl")
    private String imageUrl;

    /**
     * Родительская категория растения.
     */
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    /**
     * Подкатегория растения.
     */
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Category subcategory;

    /**
     * Перечисление типов растений.
     */
    public enum PlantType {
        SEED,
        SEEDLING,
        TUBER,
        BULB
    }

    // --- Геттеры и сеттеры ---

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlantType getPlantType() { return plantType; }
    public void setPlantType(PlantType plantType) { this.plantType = plantType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getParentCategory() { return parentCategory; }
    public void setParentCategory(Category parentCategory) { this.parentCategory = parentCategory; }

    public Category getSubcategory() { return subcategory; }
    public void setSubcategory(Category subcategory) { this.subcategory = subcategory; }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
