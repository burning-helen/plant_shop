package com.example.plant_shop.service;

import com.example.plant_shop.model.Cart;
import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.CartItemRepository;
import com.example.plant_shop.repository.CartRepository;
import com.example.plant_shop.repository.OrderRepository;
import com.example.plant_shop.repository.UserRepository;
import com.example.plant_shop.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private CartItemRepository cartItemRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CartRepository cartRepository, OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getUser();
            }
        }

        return null;
    }


    public boolean registerUser(User user) {
        System.out.println("Регистрация пользователя: " + user.getUsername());

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getDeliveryAddress() == null || user.getDeliveryAddress().isEmpty()) {
            user.setDeliveryAddress("None");
        }

        userRepository.save(user);
        return true;
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }


    @Transactional
    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElse(null);
    }

    @Transactional
    public void deleteUserWithAllData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 1. Удаляем только заказы этого пользователя
        List<Order> userOrders = orderRepository.findByUser(user);
        for (Order order : userOrders) {
            // Очищаем элементы заказа
            order.getItems().clear();
            orderRepository.save(order);
            // Удаляем сам заказ
            orderRepository.delete(order);
        }

        // 2. Удаляем корзину пользователя
        Optional<Cart> userCart = cartRepository.findByUser(user);
        if (userCart.isPresent()) {
            Cart cart = userCart.get();
            // Удаляем элементы корзины
            cartItemRepository.deleteAllByCart(cart);
            // Удаляем саму корзину
            cartRepository.delete(cart);
        }

        // 3. Удаляем пользователя
        userRepository.delete(user);
    }
}
