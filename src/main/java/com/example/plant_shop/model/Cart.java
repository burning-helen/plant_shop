package com.example.plant_shop.model;

import jakarta.persistence.*;
import java.util.Iterator;
import java.util.List;

/**
 * Модель корзины покупок пользователя.
 * Содержит список товаров, добавленных в корзину, информацию о пользователе и общую сумму.
 */
@Entity
@Table(name = "carts")
public class Cart {

    /**
     * Конструктор без аргументов (используется JPA).
     */
    public Cart() {}

    /**
     * Конструктор с параметрами.
     *
     * @param id идентификатор корзины
     * @param user пользователь, владелец корзины
     * @param items список товаров в корзине
     * @param totalAmount общая сумма корзины
     */
    public Cart(Long id, User user, List<CartItem> items, double totalAmount) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    /**
     * Уникальный идентификатор корзины.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "cart_seq", sequenceName = "carts_sequence", allocationSize = 1)
    @Column(name= "id")
    private Long id;

    /**
     * Пользователь, которому принадлежит корзина.
     * Связь один к одному.
     */
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Список товаров в корзине.
     * Связь один ко многим с каскадным удалением.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    /**
     * Общая стоимость товаров в корзине.
     */
    @Column(name = "totalAmount")
    private double totalAmount;

    // --- Геттеры и сеттеры ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Удаляет товар из корзины по его ID.
     * После удаления пересчитывает общую сумму.
     *
     * @param itemId идентификатор товара для удаления
     * @throws IllegalArgumentException если товар не найден в корзине
     */
    public void removeItem(Long itemId) {
        Iterator<CartItem> iterator = this.getItems().iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getId().equals(itemId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new IllegalArgumentException("Товар не найден в корзине");
        }
        calculateTotal();
    }

    /**
     * Пересчитывает общую стоимость товаров в корзине.
     */
    private void calculateTotal() {
        double total = this.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        this.setTotalAmount(total);
    }
}
