package com.example.plant_shop.controller;

import com.example.plant_shop.model.Order;
import com.example.plant_shop.model.OrderForm;
import com.example.plant_shop.model.User;
import com.example.plant_shop.service.OrderService;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;


@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        model.addAttribute("orderForm", new OrderForm());
        return "checkout";
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> processOrder(@ModelAttribute OrderForm orderForm, HttpSession session) {
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
}
