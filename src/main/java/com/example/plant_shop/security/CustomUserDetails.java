package com.example.plant_shop.security;

import com.example.plant_shop.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Реализация интерфейса {@link UserDetails} для интеграции пользовательской сущности {@link User}
 * с механизмом аутентификации Spring Security.
 * <p>
 * Этот класс предоставляет Spring Security доступ к учетным данным, ролям и состоянию пользователя.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Создает объект {@code CustomUserDetails}, оборачивающий {@link User} и предоставляющий права доступа.
     *
     * @param user        сущность пользователя
     * @param authorities коллекция прав доступа пользователя (ролей)
     */
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    /**
     * Возвращает обернутый объект {@link User}.
     *
     * @return пользовательская сущность
     */
    public User getUser() {
        return user;
    }

    /**
     * Возвращает коллекцию ролей/разрешений пользователя.
     *
     * @return список прав доступа
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает имя пользователя (логин).
     *
     * @return логин
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Указывает, что учетная запись не просрочена.
     *
     * @return всегда {@code true}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, что учетная запись не заблокирована.
     *
     * @return всегда {@code true}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, что учетные данные не просрочены.
     *
     * @return всегда {@code true}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, что пользователь включен (активен).
     *
     * @return всегда {@code true}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
