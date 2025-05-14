package com.example.plant_shop.config;

import com.example.plant_shop.security.CustomAuthenticationSuccessHandler;
import com.example.plant_shop.service.CartService;
import com.example.plant_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Конфигурация безопасности приложения.
 * <p>
 * Этот класс настраивает безопасность веб-приложения с использованием Spring Security. Он включает
 * настройку защиты маршрутов, обработку аутентификации и настроек сессии. Для аутентификации используется
 * форма входа с успешным редиректом через {@link CustomAuthenticationSuccessHandler}.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Метод для настройки цепочки фильтров безопасности.
     * <p>
     * В этом методе конфигурируются HTTP запросы, включая разрешение доступа к публичным страницам,
     * настройку страниц входа/выхода, а также конфигурацию аутентификации.
     * </p>
     *
     * @param http конфигурация безопасности HTTP запросов.
     * @param customAuthenticationSuccessHandler обработчик успешной аутентификации.
     * @return настроенная цепочка фильтров безопасности.
     * @throws Exception если возникнет ошибка при конфигурации безопасности.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/catalog/**", "/login", "/register", "/uploads/**", "/cart/**", "/author", "/about").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/catalog")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Метод для создания {@link PasswordEncoder} для кодирования паролей.
     * <p>
     * В данном случае используется {@link BCryptPasswordEncoder} для безопасного кодирования паролей.
     * </p>
     *
     * @return экземпляр {@link PasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Метод для создания экземпляра {@link CustomAuthenticationSuccessHandler}.
     * <p>
     * Этот обработчик используется для выполнения действий после успешной аутентификации пользователя.
     * </p>
     *
     * @param userService сервис для работы с пользователями.
     * @param cartService сервис для работы с корзинами.
     * @return настроенный {@link CustomAuthenticationSuccessHandler}.
     */
    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler(UserService userService,
                                                                                 CartService cartService) {
        return new CustomAuthenticationSuccessHandler(userService, cartService);
    }
}
