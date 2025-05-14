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

/**
 * Контроллер для регистрации пользователя.
 * <p>
 * Этот контроллер обрабатывает страницы регистрации и входа, а также регистрацию нового пользователя.
 * </p>
 */
@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    /**
     * Отображает страницу входа пользователя.
     * <p>
     * Этот метод используется для отображения формы входа на сайт.
     * </p>
     *
     * @return имя шаблона для страницы входа
     */
    @GetMapping("/login")
    public String personlogin() {
        return "login";
    }

    /**
     * Отображает страницу регистрации пользователя.
     * <p>
     * Этот метод отображает форму регистрации для нового пользователя.
     * </p>
     *
     * @param model модель для передачи данных на страницу
     * @return имя шаблона для страницы регистрации
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user_for_reg", new User());

        return "register";
    }

    /**
     * Обрабатывает форму регистрации нового пользователя.
     * <p>
     * Этот метод выполняет валидацию введённых данных, проверку на существование пользователя с таким логином,
     * и если все проверки пройдены, регистрирует нового пользователя.
     * </p>
     *
     * @param user объект, содержащий данные нового пользователя
     * @param bindingResult объект, содержащий результаты валидации данных
     * @return перенаправление на страницу входа или снова на форму регистрации с ошибками
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user_for_reg") User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.usernameExists(user.getUsername())) {
            bindingResult.rejectValue(
                    "username",
                    "username.exists",
                    "Пользователь с таким логином уже существует"
            );
            return "register";
        }

        user.setDeliveryAddress("Адрес по умолчанию");
        user.setRole(Set.of("ROLE_USER"));

        userService.registerUser(user);

        return "redirect:/login";
    }
}
