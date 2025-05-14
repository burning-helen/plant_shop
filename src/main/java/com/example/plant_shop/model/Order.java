package com.example.plant_shop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Модель заказа пользователя.
 * Содержит информацию о товарах, пользователе, способе оплаты, дате, адресе доставки и статусе.
 */
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Конструктор по умолчанию.
     */
    public Order() {}

    /**
     * Конструктор с параметрами.
     * @param id идентификатор заказа
     * @param user пользователь, оформивший заказ
     * @param paymentMethod способ оплаты
     * @param orderDate дата и время оформления заказа
     * @param deliveryAddress адрес доставки
     * @param status текущий статус заказа
     * @param totalAmount общая сумма заказа
     */
    public Order(long id, User user, String paymentMethod, LocalDateTime orderDate, String deliveryAddress, String status, double totalAmount) {
        this.id = id;
        this.user = user;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    /**
     * Уникальный идентификатор заказа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "order_seq", sequenceName = "order_sequence", allocationSize = 1)
    @Column(name= "id")
    private long id;

    /**
     * Список позиций в заказе.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> items;

    /**
     * Пользователь, оформивший заказ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Дата и время оформления заказа.
     */
    @Column(name = "orderDate")
    private LocalDateTime orderDate;

    /**
     * Адрес доставки.
     */
    @Column(name = "deliveryAddress")
    private String deliveryAddress;

    /**
     * Способ оплаты.
     */
    @Column(name = "paymentMethod")
    private String paymentMethod;

    /**
     * Статус заказа (например: "Новый", "Обрабатывается", "Доставлен").
     */
    @Column(name = "status")
    private String status;

    /**
     * Общая сумма заказа.
     */
    @Column(name = "totalAmount")
    private double totalAmount;

    // --- Геттеры и сеттеры ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
