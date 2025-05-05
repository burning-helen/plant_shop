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

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String profilePage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }

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

