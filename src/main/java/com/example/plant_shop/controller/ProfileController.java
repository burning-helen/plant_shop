package com.example.plant_shop.controller;

import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.UserRepository;
import com.example.plant_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Контроллер для управления профилем пользователя.
 * <p>
 * Этот контроллер предоставляет функциональность для отображения профиля пользователя,
 * обновления личных данных, а также изменения пароля.
 * </p>
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    /**
     * Отображает страницу профиля пользователя.
     * <p>
     * Этот метод используется для отображения информации о текущем пользователе.
     * </p>
     *
     * @param model модель для передачи данных на страницу
     * @param principal текущий аутентифицированный пользователь
     * @return имя шаблона для отображения профиля пользователя
     */
    @GetMapping
    public String profilePage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Обновляет профиль пользователя.
     * <p>
     * Этот метод обновляет личные данные пользователя, такие как имя, фамилия, email, адрес доставки, телефон и возраст.
     * После успешного обновления, пользователя перенаправляют на страницу профиля с сообщением об успехе.
     * </p>
     *
     * @param updatedUser объект, содержащий обновленные данные пользователя
     * @param principal текущий аутентифицированный пользователь
     * @param redirectAttributes атрибуты для перенаправления с сообщением об успехе
     * @return перенаправление на страницу профиля с сообщением об успехе
     */
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User updatedUser, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setDeliveryAddress(updatedUser.getDeliveryAddress());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAge(updatedUser.getAge());

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Профиль обновлён");
        return "redirect:/profile";
    }

    /**
     * Изменяет пароль пользователя.
     * <p>
     * Этот метод позволяет пользователю изменить свой пароль. Для этого необходимо ввести старый пароль, новый пароль и подтверждение нового пароля.
     * После успешной смены пароля пользователя перенаправляют на страницу профиля с сообщением об успехе.
     * </p>
     *
     * @param oldPassword старый пароль пользователя
     * @param newPassword новый пароль пользователя
     * @param confirmPassword подтверждение нового пароля
     * @param principal текущий аутентифицированный пользователь
     * @param redirectAttributes атрибуты для перенаправления с сообщениями об ошибке или успехе
     * @return перенаправление на страницу профиля с соответствующим сообщением
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Старый пароль неверный");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Новый пароль и подтверждение не совпадают");
            return "redirect:/profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Пароль успешно изменён");
        return "redirect:/profile";
    }
}
