package com.example.plant_shop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для обработки главных страниц сайта и выхода из системы.
 * <p>
 * Этот контроллер управляет отображением таких страниц, как главная страница, страница об авторе,
 * страница о сайте и обработка выхода из системы.
 * </p>
 */
@Controller
public class HomeController {

    /**
     * Перенаправляет пользователя на страницу каталога.
     *
     * @return URL для редиректа на каталог товаров
     */
    @GetMapping("/")
    public String startPage() {
        return "redirect:/catalog"; // Перенаправление на каталог товаров
    }

    /**
     * Отображает главную страницу сайта.
     *
     * @return имя шаблона для отображения главной страницы
     */
    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    /**
     * Отображает страницу о авторе сайта.
     *
     * @return имя шаблона для отображения страницы об авторе
     */
    @GetMapping("/author")
    public String authorPage() {
        return "author"; // Шаблон страницы об авторе
    }

    /**
     * Отображает страницу о сайте.
     *
     * @return имя шаблона для отображения страницы о сайте
     */
    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    /**
     * Обрабатывает выход пользователя из системы и перенаправляет на каталог товаров.
     *
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @return редирект на каталог товаров после выхода
     */
    @GetMapping("/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/catalog";
    }
}
