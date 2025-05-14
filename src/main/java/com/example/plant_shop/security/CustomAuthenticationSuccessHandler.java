package com.example.plant_shop.security;

import com.example.plant_shop.model.User;
import com.example.plant_shop.service.CartService;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final CartService cartService;

    public CustomAuthenticationSuccessHandler(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = userService.getCurrentUser();

        cartService.getCurrentCart(session, user);

        response.sendRedirect("/catalog");
    }
}
