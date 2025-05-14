package com.example.plant_shop.service;

import com.example.plant_shop.model.User;
import com.example.plant_shop.repository.UserRepository;
import com.example.plant_shop.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Сервис для загрузки данных пользователя с помощью Spring Security.
 * <p>
 * Этот сервис реализует интерфейс {@link UserDetailsService}, который используется для загрузки данных пользователя по имени пользователя.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Загружает пользователя по имени пользователя.
     * <p>
     * Используется для аутентификации и авторизации пользователя в приложении. Загружает пользователя из базы данных и возвращает
     * {@link CustomUserDetails}, который содержит все необходимые данные для работы с Spring Security.
     * </p>
     *
     * @param username имя пользователя
     * @return объект {@link UserDetails}, который содержит информацию о пользователе и его ролях
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(
                user,
                user.getRole().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}
