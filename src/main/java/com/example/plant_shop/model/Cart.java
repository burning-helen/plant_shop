package com.example.plant_shop.model;


import jakarta.persistence.*;

import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
    public Cart() {}

    public Cart(Long id, User user, List<CartItem> items, double totalAmount) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "cart_seq", sequenceName = "carts_sequence", allocationSize = 1)
    @Column(name= "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    @Column(name = "totalAmount")
    private double totalAmount;

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


    private void calculateTotal() {
        double total = this.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        this.setTotalAmount(total);
    }


}
