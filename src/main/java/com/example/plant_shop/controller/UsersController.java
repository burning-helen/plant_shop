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

/**
 * Контроллер для управления пользователями в административной панели.
 * <p>
 * Этот контроллер отвечает за добавление, редактирование, удаление и отображение списка пользователей
 * в административной панели, а также за проверку прав на удаление.
 * </p>
 */
@Controller
@RequestMapping("/admin/users")
public class UsersController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    /**
     * Отображает страницу управления пользователями.
     * <p>
     * Этот метод загружает список всех пользователей и отображает форму для редактирования или добавления пользователя.
     * </p>
     *
     * @param editId ID пользователя, которого нужно отредактировать (если указан)
     * @param model модель для передачи данных на страницу
     * @param authentication информация о текущем пользователе
     * @return имя шаблона для страницы управления пользователями
     */
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

    /**
     * Сохраняет изменения в данных пользователя или добавляет нового пользователя.
     * <p>
     * Этот метод проверяет уникальность логина, обязательность пароля для новых пользователей и обновляет
     * данные пользователя в базе данных.
     * </p>
     *
     * @param user объект с данными пользователя, которые нужно сохранить или обновить
     * @param model модель для передачи данных на страницу
     * @return перенаправление на страницу управления пользователями
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user_for_reg") User user,
                           Model model) {
        boolean isNew = (user.getId() == null);

        if (isNew) {
            if (userRepo.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Логин «" + user.getUsername() + "» уже используется");
                model.addAttribute("editing", false);
                model.addAttribute("users", userRepo.findAll());
                model.addAttribute("user_for_reg", user);

                return "admin/users_management";
            }
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
            User existing = userRepo.findById(user.getId()).orElseThrow();
            if (!existing.getUsername().equals(user.getUsername())
                    && userRepo.findByUsername(user.getUsername()).isPresent()) {
                model.addAttribute("error", "Логин «" + user.getUsername() + "» уже используется");
                model.addAttribute("editing", true);
                model.addAttribute("user_for_reg", existing);
                model.addAttribute("users", userRepo.findAll());
                return "admin/users_management";
            }
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

    /**
     * Удаляет пользователя из базы данных.
     * <p>
     * Этот метод удаляет пользователя, проверяя, не пытается ли администратор удалить своего собственного аккаунта.
     * </p>
     *
     * @param id ID пользователя, которого нужно удалить
     * @param authentication информация о текущем пользователе
     * @param redirectAttributes атрибуты для перенаправления с сообщениями о результате операции
     * @return перенаправление на страницу управления пользователями
     */
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

    /**
     * Отображает страницу с подтверждением удаления пользователя.
     * <p>
     * Этот метод показывает страницу с подтверждением удаления для указанного пользователя.
     * </p>
     *
     * @param id ID пользователя, которого нужно удалить
     * @param model модель для передачи данных на страницу
     * @param authentication информация о текущем пользователе
     * @return имя шаблона для страницы подтверждения удаления
     */
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id,
                                Model model,
                                Authentication authentication) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный ID пользователя: " + id));

        if (user.getUsername().equals(authentication.getName())) {
            model.addAttribute("error",
                    "Вы не можете удалить свой собственный аккаунт.");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "admin/delete_user_confirm";
    }

    /**
     * Отображает форму добавления нового пользователя.
     * <p>
     * Этот метод отображает форму для добавления нового пользователя в административной панели.
     * </p>
     *
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для страницы добавления пользователя
     */
    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("editing", false);
        return "admin/users_management";
    }

    /**
     * Отображает форму редактирования данных существующего пользователя.
     * <p>
     * Этот метод отображает форму для редактирования данных пользователя в административной панели.
     * </p>
     *
     * @param id ID пользователя, чьи данные нужно редактировать
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для страницы редактирования пользователя
     */
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
