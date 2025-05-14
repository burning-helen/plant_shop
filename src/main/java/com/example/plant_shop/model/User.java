package com.example.plant_shop.model;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.util.*;

/**
 * Сущность пользователя в системе интернет-магазина растений.
 * Содержит личные данные, контактную информацию, роли, а также связанные корзину и заказы.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Конструктор по умолчанию.
     */
    public User() {}

    /**
     * Конструктор с параметрами.
     * @param id Идентификатор
     * @param firstName Имя
     * @param lastName Фамилия
     * @param age Возраст
     * @param email Email
     * @param username Логин
     * @param password Пароль
     * @param phoneNumber Телефон
     */
    public User(long id, String firstName, String lastName, int age, String email,
                String username, String password,
                String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = Set.of("ROLE_USER");
        this.deliveryAddress = "Адрес по умолчанию";
        this.phoneNumber = phoneNumber;
    }

    /** Уникальный идентификатор пользователя */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /** Имя пользователя */
    @NotBlank(message = "Имя обязательно")
    @Column(name = "firstName")
    private String firstName;

    /** Фамилия пользователя */
    @NotBlank(message = "Фамилия обязательна")
    @Column(name = "lastName")
    private String lastName;

    /** Возраст пользователя */
    @Min(value = 1, message = "Возраст должен быть больше 0")
    @Max(value = 120, message = "Возраст должен быть меньше 120")
    @Column(name = "age")
    private int age;

    /** Email пользователя */
    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    @Column(name = "email")
    private String email;

    /** Логин пользователя */
    @NotBlank(message = "Логин обязателен")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /** Пароль пользователя */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Column(name = "password", nullable = false)
    private String password;

    /** Телефонный номер пользователя */
    @NotBlank(message = "Номер телефона обязателен")
    @Column(name = "phone_number")
    private String phoneNumber;

    /** Адрес доставки пользователя */
    @Column(name = "delivery_address")
    private String deliveryAddress;

    /** Роли пользователя (например, ROLE_USER, ROLE_ADMIN) */
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> role;

    /** Корзина пользователя (один-к-одному) */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    /** Список заказов пользователя */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // Геттеры и сеттеры

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<String> getRole() { return role; }
    public void setRole(Set<String> role) { this.role = role; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}
