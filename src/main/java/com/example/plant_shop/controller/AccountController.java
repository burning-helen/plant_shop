package com.example.plant_shop.controller;

import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.UserRepository;
import com.example.plant_shop.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AccountController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String account(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин" + username));
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/account/edit/{username}")
    public String showEditeUserForm(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин" + username));
        model.addAttribute("user", user);
        return "edit_account";
    }

    @PostMapping("/account/edit/{username}")
    public String editeUser(@PathVariable String username, @ModelAttribute("user") User editedUser) {
        User existingUser = userRepo.findByUsername(editedUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Неверный Id: " + editedUser.getId()));

        if (editedUser.getFirstName() != null && !editedUser.getFirstName().isEmpty()) {
            existingUser.setFirstName(editedUser.getFirstName());
        }
        if (editedUser.getLastName() != null && !editedUser.getLastName().isEmpty()) {
            existingUser.setLastName(editedUser.getLastName());
        }
        if (editedUser.getAge() != 0) {
            existingUser.setAge(editedUser.getAge());
        }
        if (editedUser.getEmail() != null && !editedUser.getEmail().isEmpty()) {
            existingUser.setEmail(editedUser.getEmail());
        }
        if (editedUser.getUsername() != null && !editedUser.getUsername().isEmpty()) {
            existingUser.setUsername(editedUser.getUsername());
        }

        userRepo.save(existingUser);
        return "redirect:/account";
    }

    @GetMapping("/account/delete")
    public String deleteUser(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин" + username));
        userRepo.deleteById(user.getId());
        return "redirect:/home";
    }

    @PostMapping("/account/delete")
    public String deleteUser(Authentication authentication, HttpServletRequest request) throws ServletException {
        String username = authentication.getName();
        userService.deleteUserByUsername(username);

        request.logout();
        return "redirect:/login?delete";
    }

}

