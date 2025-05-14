package com.example.plant_shop.service;

import com.example.plant_shop.model.*;
import com.example.plant_shop.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления заказами.
 * <p>
 * Этот сервис предоставляет методы для оформления заказа, получения списка заказов пользователя и администрирования корзины.
 * </p>
 */
@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    /**
     * Оформление нового заказа.
     * <p>
     * Этот метод создаёт новый заказ на основе выбранных товаров из корзины и данных пользователя. После оформления заказа,
     * обновляется количество товаров на складе и корзина. Если товар не доступен, он пропускается.
     * </p>
     *
     * @param orderForm      данные формы для оформления заказа (адрес доставки, метод оплаты и т.д.)
     * @param selectedItemIds список идентификаторов выбранных товаров из корзины
     * @param session        сессия пользователя
     * @param model          модель для передачи данных в представление
     * @throws IllegalStateException если в корзине нет доступных товаров для заказа
     */
    @Transactional
    public void placeOrder(OrderForm orderForm, List<Long> selectedItemIds, HttpSession session, Model model) {
        User user = userService.getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUserAndIdIn(user, selectedItemIds);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Нет выбранных товаров для заказа");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(orderForm.getDeliveryAddress());
        order.setPaymentMethod(orderForm.getPaymentMethod());
        order.setStatus("В обработке");

        double total = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            if (!cartItem.getPlant().isActive() || cartItem.getPlant().getStockQuantity() < cartItem.getQuantity()) {
                continue; // Пропускаем неактивные товары или товары с недостаточным запасом
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setPlant(cartItem.getPlant());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getPlant().getPrice());
            total += item.getPrice() * item.getQuantity();
            orderItems.add(item);

            // Обновляем количество на складе
            Plant plant = cartItem.getPlant();
            plant.setStockQuantity(plant.getStockQuantity() - cartItem.getQuantity());
            plantRepository.save(plant);
        }

        if (orderItems.isEmpty()) {
            throw new IllegalStateException("Нет доступных товаров для заказа");
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        orderRepository.save(order);

        Cart cart = (Cart) cartService.getCurrentCart(session, user);

        if (user != null) {
            // Для авторизованных пользователей
            cartItemRepository.deleteAllById(selectedItemIds);
            cart.getItems().removeIf(item -> selectedItemIds.contains(item.getId()));
            cartRepository.save(cart);
        } else {
            // Для неавторизованных пользователей
            cart.getItems().removeIf(item -> selectedItemIds.contains(item.getId()));
            session.setAttribute("cart", cart);
        }

        // Пересчет общей суммы корзины
        double newTotal = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(newTotal);

        if (user != null) {
            cartRepository.save(cart);
        } else {
            session.setAttribute("cart", cart);
        }

        model.addAttribute("cartCount", cartService.getCartItemCount(session));

    }

    /**
     * Получает все заказы пользователя.
     * <p>
     * Этот метод возвращает все заказы для указанного пользователя, включая их товары.
     * </p>
     *
     * @param user пользователь, чьи заказы нужно получить
     * @return список заказов пользователя
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findWithItemsByUser(user);
    }

    /**
     * Получает все заказы в системе.
     * <p>
     * Этот метод возвращает все заказы в системе, отсортированные по дате (от новых к старым).
     * </p>
     *
     * @return список всех заказов
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
}
