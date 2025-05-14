package com.example.plant_shop.controller;

import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.OrderForm;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.OrderRepository;
import com.example.plant_shop.repository.UserRepository;
import com.example.plant_shop.service.CartService;
import com.example.plant_shop.service.OrderService;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для обработки заказов пользователей.
 * <p>
 * Этот контроллер управляет процессом оформления заказов, отображением истории заказов пользователя и администратора,
 * а также обновлением статуса заказов администратором.
 * </p>
 */
@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Отображает страницу оформления заказа.
     * <p>
     * Пользователь может увидеть выбранные товары и общую сумму заказа.
     * Если пользователь авторизован, отображается его адрес доставки.
     * </p>
     *
     * @param model модель для передачи данных на страницу
     * @param principal текущий аутентифицированный пользователь
     * @param selectedItemIds список выбранных товаров для оформления
     * @return имя шаблона для страницы оформления заказа
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal,
                                   @RequestParam("selectedItemIds") List<Long> selectedItemIds) {
        // Добавляем проверку, что выбранные товары есть
        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("orderForm", new OrderForm());
        model.addAttribute("selectedItems", cartService.getItemsByIds(selectedItemIds));
        model.addAttribute("totalAmount", cartService.calculateTotal(selectedItemIds));

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null && user.getDeliveryAddress() != null && !user.getDeliveryAddress().isBlank()) {
                model.addAttribute("userAddress", user.getDeliveryAddress());
            }
        }

        return "checkout";
    }

    /**
     * Обрабатывает оформление заказа.
     * <p>
     * При успешном оформлении, сохраняется адрес доставки пользователя и заказ добавляется в базу данных.
     * </p>
     *
     * @param orderForm форма с данными заказа
     * @param model модель для передачи данных на страницу
     * @param selectedItemIds список выбранных товаров для оформления
     * @param session HTTP сессия
     * @param principal текущий аутентифицированный пользователь
     * @return имя шаблона для страницы оформления заказа с благодарностью
     */
    @PostMapping("/checkout")
    public String processOrder(@ModelAttribute OrderForm orderForm, Model model,
                               @RequestParam(value = "selectedItemIds", required = false) List<Long> selectedItemIds,
                               HttpSession session,
                               Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                user.setDeliveryAddress(orderForm.getDeliveryAddress());
                userRepository.save(user);
            }
        }

        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
            return "redirect:/cart";
        }
        orderService.placeOrder(orderForm, selectedItemIds, session, model);
        model.addAttribute("thankYou", true);
        return "checkout";
    }

    /**
     * Отображает историю заказов текущего пользователя.
     *
     * @param model модель для передачи данных на страницу
     * @param principal текущий аутентифицированный пользователь
     * @return имя шаблона для отображения истории заказов пользователя
     */
    @GetMapping("/user_orders")
    public String userOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "user_orders";
    }

    /**
     * Отображает список всех заказов для администратора.
     *
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для отображения всех заказов
     */
    @GetMapping("/admin_orders")
    public String adminOrders(Model model) {
        List<Order> allOrders = orderService.getAllOrders();
        model.addAttribute("orders", allOrders);
        return "admin/admin_orders";
    }

    /**
     * Обновляет статус заказа.
     * <p>
     * Этот метод позволяет администратору обновлять статус заказа.
     * </p>
     *
     * @param orderId идентификатор заказа
     * @param status новый статус заказа
     * @return редирект на страницу с заказами администратора
     */
    @PostMapping("/admin/orders/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam String status) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
        }
        return "redirect:/admin_orders";
    }
}
