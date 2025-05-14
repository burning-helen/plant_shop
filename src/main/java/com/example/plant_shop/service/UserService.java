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

/**
 * Сервис для управления пользователями.
 * <p>
 * Этот сервис предоставляет методы для регистрации, удаления пользователей,
 * а также для работы с текущим пользователем, его корзиной и заказами.
 * </p>
 */
@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private CartItemRepository cartItemRepository;

    /**
     * Конструктор для инициализации сервисов и репозиториев.
     *
     * @param userRepository репозиторий для работы с пользователями
     * @param passwordEncoder кодировщик паролей
     * @param cartRepository репозиторий для работы с корзинами
     * @param orderRepository репозиторий для работы с заказами
     * @param cartItemRepository репозиторий для работы с элементами корзины
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CartRepository cartRepository, OrderRepository orderRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Получает текущего авторизованного пользователя.
     * <p>
     * Этот метод извлекает текущего пользователя из контекста безопасности и возвращает его объект.
     * Если пользователь не авторизован, возвращается null.
     * </p>
     *
     * @return текущий пользователь или null, если пользователь не авторизован
     */
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

    /**
     * Регистрирует нового пользователя.
     * <p>
     * Этот метод проверяет, существует ли уже пользователь с данным именем,
     * и если нет, сохраняет его в базе данных.
     * </p>
     *
     * @param user объект пользователя для регистрации
     * @return true, если регистрация успешна, иначе false
     */
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

    /**
     * Проверяет, существует ли уже пользователь с данным именем.
     * <p>
     * Этот метод ищет пользователя по имени и возвращает true,
     * если такой пользователь уже зарегистрирован.
     * </p>
     *
     * @param username имя пользователя
     * @return true, если пользователь существует, иначе false
     */
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Находит пользователя по имени.
     * <p>
     * Этот метод ищет пользователя по его имени и возвращает найденного пользователя,
     * или null, если пользователь не найден.
     * </p>
     *
     * @param name имя пользователя
     * @return найденный пользователь или null
     */
    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElse(null);
    }

    /**
     * Удаляет пользователя и все связанные с ним данные.
     * <p>
     * Этот метод удаляет все заказы, корзину и самого пользователя из базы данных.
     * </p>
     *
     * @param userId идентификатор пользователя, которого нужно удалить
     * @throws EntityNotFoundException если пользователь с данным идентификатором не найден
     */
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
