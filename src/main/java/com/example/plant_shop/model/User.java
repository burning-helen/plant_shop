package com.example.plant_shop.model;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
public class User {
    public User() {}

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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Column(name = "firstName")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Column(name = "lastName")
    private String lastName;

    @Min(value = 1, message = "Возраст должен быть больше 0")
    @Max(value = 120, message = "Возраст должен быть меньше 120")
    @Column(name = "age")
    private int age;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Логин обязателен")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Номер телефона обязателен")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();


    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

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

}
