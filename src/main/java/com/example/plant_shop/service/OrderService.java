package com.example.plant_shop.service;

import com.example.plant_shop.model.*;
import com.example.plant_shop.repository.CartItemRepository;
import com.example.plant_shop.repository.OrderRepository;
import com.example.plant_shop.repository.PlantRepository;
import com.example.plant_shop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void placeOrder(OrderForm orderForm, HttpSession session) {
        User user = userService.getCurrentUser();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(orderForm.getDeliveryAddress());
        order.setPaymentMethod(orderForm.getPaymentMethod());
        order.setStatus("В обработке");


        double total = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setPlant(cartItem.getPlant());
            item.setQuantity(cartItem.getQuantity());
            cartItem.getPlant().setStockQuantity(cartItem.getPlant().getStockQuantity() - cartItem.getQuantity());
            item.setPrice(cartItem.getPlant().getPrice());
            total += item.getPrice() * item.getQuantity();
            orderItems.add(item);
        }

        order.setTotalAmount(total);
        order.setItems(orderItems);

        orderRepository.save(order); // cascade сохранит orderItems
        cartService.clearCart(session);

    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findWithItemsByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }


}
