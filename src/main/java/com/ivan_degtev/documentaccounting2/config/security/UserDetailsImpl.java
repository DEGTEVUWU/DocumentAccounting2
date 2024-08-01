package com.ivan_degtev.documentaccounting2.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ivan_degtev.documentaccounting2.model.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Этот класс служит для хранения информации о пользователе, которая затем используется для аутентификации и авторизации.
 * Основная цель этого класса — адаптировать пользовательскую модель юзера к интерфейсу, который понимает Spring Security.
 */
@Setter
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Метод преобразует объект пользователя в объект UserDetailsImpl.
     * В лоигике исползуется в связке из основного фильтра секьюрити doFilterInternal - создается объект UserDetails,
     * путем вызова утилитарного метода loadUserByUsername из сервисного слоя пользователей,
     * который билдит объект, наследующий UserDetails
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getIdUser(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    /**
     * Ниже  реализация стандартных методов по получению состояния аккаунта текущего юзера для секьюрити
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
