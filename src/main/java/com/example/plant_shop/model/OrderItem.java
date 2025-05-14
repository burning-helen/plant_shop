package com.example.plant_shop.model;

import jakarta.persistence.*;

/**
 * Сущность элемента заказа, представляющая конкретное растение, заказанное пользователем,
 * с указанием количества и цены на момент оформления.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    /**
     * Конструктор по умолчанию.
     */
    public OrderItem() {}

    /**
     * Уникальный идентификатор элемента заказа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "order_seq", sequenceName = "order_sequence", allocationSize = 1)
    @Column(name= "id")
    private long id;

    /**
     * Связанный заказ, к которому относится этот элемент.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Растение, заказанное пользователем.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    /**
     * Количество единиц растения в заказе.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Цена за единицу растения на момент оформления заказа.
     */
    @Column(nullable = false)
    private double price;

    /**
     * Получить ID элемента заказа.
     * @return уникальный идентификатор
     */
    public long getId() { return id; }

    /**
     * Установить ID элемента заказа.
     * @param id уникальный идентификатор
     */
    public void setId(long id) { this.id = id; }

    /**
     * Получить заказ, к которому относится элемент.
     * @return объект заказа
     */
    public Order getOrder() { return order; }

    /**
     * Установить заказ, к которому относится элемент.
     * @param order объект заказа
     */
    public void setOrder(Order order) { this.order = order; }

    /**
     * Получить растение, связанное с элементом заказа.
     * @return объект растения
     */
    public Plant getPlant() { return plant; }

    /**
     * Установить растение, связанное с элементом заказа.
     * @param plant объект растения
     */
    public void setPlant(Plant plant) { this.plant = plant; }

    /**
     * Получить количество растений.
     * @return количество
     */
    public Integer getQuantity() { return quantity; }

    /**
     * Установить количество растений.
     * @param quantity количество
     */
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    /**
     * Получить цену за единицу растения.
     * @return цена
     */
    public double getPrice() { return price; }

    /**
     * Установить цену за единицу растения.
     * @param price цена
     */
    public void setPrice(double price) { this.price = price; }
}
