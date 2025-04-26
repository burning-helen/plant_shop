package com.example.plant_shop.model;


import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    public OrderItem() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "order_seq", sequenceName = "order_sequence", allocationSize = 1)
    @Column(name= "id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;


    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private double price;
}
