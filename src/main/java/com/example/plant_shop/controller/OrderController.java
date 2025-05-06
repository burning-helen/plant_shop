package com.example.plant_shop.controller;

import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.OrderForm;
import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.OrderRepository;
import com.example.plant_shop.repository.UserRepository;
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


@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        model.addAttribute("orderForm", new OrderForm());

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null && user.getDeliveryAddress() != null && !user.getDeliveryAddress().isBlank()) {
                model.addAttribute("userAddress", user.getDeliveryAddress());
            }
        }

        return "checkout";
    }


    @PostMapping("/checkout")
    public ResponseEntity<String> processOrder(@ModelAttribute OrderForm orderForm,
                                               HttpSession session,
                                               Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                user.setDeliveryAddress(orderForm.getDeliveryAddress());
                userRepository.save(user);
            }
        }

        orderService.placeOrder(orderForm, session);
        return ResponseEntity.ok("Заказ успешно оформлен");
    }

    @GetMapping("/user_orders")
    public String userOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "user_orders";
    }

    @GetMapping("/admin_orders")
    public String adminOrders(Model model) {
        List<Order> allOrders = orderService.getAllOrders();
        model.addAttribute("orders", allOrders);
        return "admin/admin_orders";
    }

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
