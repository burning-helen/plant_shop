package com.example.plant_shop.controller;

import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UsersController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Страница управления пользователями
    @GetMapping
    public String usersManagement(@RequestParam(required = false) Long editId, Model model, Authentication authentication) {
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);

        User user = (editId != null)
                ? userRepo.findById(editId).orElse(new User())
                : new User();

        model.addAttribute("user", user);
        model.addAttribute("editing", editId != null);
        model.addAttribute("currentUsername", authentication.getName());

        return "admin/users_management";
    }

    // Сохранение (нового или отредактированного) пользователя
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        if (user.getId() == null) {
            // Новый пользователь — обязательно установить пароль (захешируй!)
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Пароль обязателен для новых пользователей");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Существующий пользователь — не обновляем пароль
            User existing = userRepo.findById(user.getId()).orElseThrow();
            user.setPassword(existing.getPassword());
        }

        userRepo.save(user);
        return "redirect:/admin/users";
    }


    // Удаление пользователя (с проверкой, что не удаляется текущий пользователь)
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String currentUsername = authentication.getName();

        try {
            User toDelete = userRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Неверный ID пользователя: " + id));

            if (toDelete.getUsername().equals(currentUsername)) {
                redirectAttributes.addFlashAttribute("error",
                        "Нельзя удалить текущего пользователя.");
                return "redirect:/admin/users";
            }

            userRepo.delete(toDelete);
            redirectAttributes.addFlashAttribute("success",
                    "Пользователь " + toDelete.getUsername() + " успешно удален.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при удалении пользователя: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id,
                                Model model,
                                Authentication authentication) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный ID пользователя: " + id));

        // Проверка, что пользователь не пытается удалить себя
        if (user.getUsername().equals(authentication.getName())) {
            model.addAttribute("error",
                    "Вы не можете удалить свой собственный аккаунт.");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/delete_user_confirm";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("editing", false);
        return "admin/users_management";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("editing", true);
        return "admin/users_management";
    }



}
