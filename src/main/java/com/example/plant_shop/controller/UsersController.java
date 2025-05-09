package com.example.plant_shop.controller;

import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.UserRepository;
import com.example.plant_shop.service.UserService;
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
    @Autowired
    private UserService userService;

    // Страница управления пользователями
    @GetMapping
    public String usersManagement(@RequestParam(required = false) Long editId, Model model, Authentication authentication) {
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);

        User user = (editId != null)
                ? userRepo.findById(editId).orElse(new User())
                : new User();

        model.addAttribute("user_for_reg", user);
        model.addAttribute("editing", editId != null);
        model.addAttribute("currentUsername", authentication.getName());

        return "admin/users_management";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user_for_reg") User user,
                           Model model) {
        boolean isNew = (user.getId() == null);

        // 1) Для нового пользователя: проверим, что логин не занят
        if (isNew) {
            if (userRepo.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Логин «" + user.getUsername() + "» уже используется");
                model.addAttribute("editing", false);
                model.addAttribute("users", userRepo.findAll());
                model.addAttribute("user_for_reg", user);

                return "admin/users_management";
            }
            // Пароль обязателен для новых
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                model.addAttribute("error", "Пароль обязателен для новых пользователей");
                model.addAttribute("editing", false);
                model.addAttribute("users", userRepo.findAll());
                model.addAttribute("user_for_reg", user);

                return "admin/users_management";
            }
            user.setPhoneNumber("+7000000000");
            user.setDeliveryAddress("Адрес по умолчанию");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);

        } else {
            // 2) Редактирование: если поменяли логин — тоже проверим уникальность
            User existing = userRepo.findById(user.getId()).orElseThrow();
            if (!existing.getUsername().equals(user.getUsername())
                    && userRepo.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Логин «" + user.getUsername() + "» уже используется");
                model.addAttribute("editing", true);
                model.addAttribute("user_for_reg", existing);
                model.addAttribute("users", userRepo.findAll());
                return "admin/users_management";
            }
            // Обновляем поля, не трогая пароль
            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());
            existing.setFirstName(user.getFirstName());
            existing.setLastName(user.getLastName());
            existing.setAge(user.getAge());
            existing.setRole(user.getRole());
            userRepo.save(existing);
        }

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

            userService.deleteUserWithAllData(id);
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
        model.addAttribute("user_for_reg", user);
        model.addAttribute("editing", true);
        return "admin/users_management";
    }



}
