package com.example.plant_shop.model;

import jakarta.persistence.*;

/**
 * Модель элемента корзины.
 * Представляет конкретный товар (растение), добавленный пользователем в корзину,
 * с указанным количеством и ценой.
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    /**
     * Конструктор без аргументов.
     */
    public CartItem() {
    }

    /**
     * Конструктор со всеми полями.
     *
     * @param id идентификатор элемента корзины
     * @param user пользователь, добавивший товар
     * @param cart корзина, к которой относится элемент
     * @param plant растение, добавленное в корзину
     * @param quantity количество растений
     * @param price цена за единицу товара на момент добавления
     */
    public CartItem(long id, User user, Cart cart, Plant plant, Integer quantity, double price) {
        this.id = id;
        this.user = user;
        this.cart = cart;
        this.plant = plant;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * Уникальный идентификатор элемента корзины.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Пользователь, добавивший товар в корзину.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Корзина, к которой принадлежит этот элемент.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * Растение, добавленное в корзину.
     */
    @ManyToOne
    private Plant plant;

    /**
     * Количество выбранного растения.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Цена за единицу растения на момент добавления.
     */
    @Column(nullable = false)
    private double price;

    // --- Геттеры и сеттеры ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
