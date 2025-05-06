package com.example.plant_shop.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "orders")
public class Order {
    public Order() {}

    public Order(long id, User user, String paymentMethod, LocalDateTime orderDate, String deliveryAddress, String status, double totalAmount) {
        this.id = id;
        this.user = user;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "order_seq", sequenceName = "order_sequence", allocationSize = 1)
    @Column(name= "id")
    private long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "orderDate")
    private LocalDateTime orderDate;

    @Column(name = "deliveryAddress")
    private String deliveryAddress;

    @Column(name = "paymentMethod")
    private String paymentMethod;

    @Column(name = "status")
    private String status;

    @Column(name = "totalAmount")
    private double totalAmount;


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
