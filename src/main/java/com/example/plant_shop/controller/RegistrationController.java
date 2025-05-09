package com.example.plant_shop.controller;

import com.example.plant_shop.model.User;
import com.example.plant_shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String personlogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user_for_reg", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user_for_reg") User user,
            BindingResult bindingResult,
            Model model) {

        // 1) Если другие поля не прошли валидацию — сразу обратно на форму
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 2) Проверяем уникальность username
        if (userService.usernameExists(user.getUsername())) {
            // связываем ошибку с полем username
            bindingResult.rejectValue(
                    "username",               // поле у User
                    "username.exists",        // код ошибки
                    "Пользователь с таким логином уже существует"
            );
            return "register";
        }

        // 3) Всё хорошо — сохраняем
        user.setDeliveryAddress("Адрес по умолчанию");
        user.setRole(Set.of("ROLE_USER"));
        userService.registerUser(user);
        return "redirect:/login";
    }
}



