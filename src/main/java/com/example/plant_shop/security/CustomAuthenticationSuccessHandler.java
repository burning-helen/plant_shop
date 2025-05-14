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

/**
 * Обработчик, вызываемый после успешной аутентификации пользователя.
 * <p>
 * Выполняет дополнительную логику после входа пользователя в систему:
 * инициализирует текущую корзину пользователя и перенаправляет на каталог.
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final CartService cartService;

    /**
     * Создает новый {@code CustomAuthenticationSuccessHandler} с необходимыми сервисами.
     *
     * @param userService сервис для получения текущего пользователя
     * @param cartService сервис для управления корзиной
     */
    public CustomAuthenticationSuccessHandler(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    /**
     * Метод вызывается при успешной аутентификации пользователя.
     * <ul>
     *     <li>Получает текущего пользователя через {@link UserService}</li>
     *     <li>Инициализирует/загружает корзину для текущего сеанса через {@link CartService}</li>
     *     <li>Перенаправляет пользователя на страницу каталога ({@code /catalog})</li>
     * </ul>
     *
     * @param request        HTTP-запрос
     * @param response       HTTP-ответ
     * @param authentication объект аутентификации Spring Security
     * @throws IOException      если произошла ошибка ввода-вывода при перенаправлении
     * @throws ServletException если возникает ошибка сервлета
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = userService.getCurrentUser();

        // Загружаем/создаём корзину пользователя в текущей сессии
        cartService.getCurrentCart(session, user);

        // Перенаправление на каталог товаров после входа
        response.sendRedirect("/catalog");
    }
}
