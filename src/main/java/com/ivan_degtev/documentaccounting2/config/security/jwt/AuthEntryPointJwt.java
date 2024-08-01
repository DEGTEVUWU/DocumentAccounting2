package com.ivan_degtev.documentaccounting2.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс AuthEntryPointJwt реализует интерфейсы AuthenticationEntryPoint и AccessDeniedHandler,
 * чтобы обрабатывать несанкционированные доступы и попытки доступа к защищенным ресурсам без нужных прав.
 * Для работы он будет внедрён в конфигруационный класс WebSecurityConfig и использоваться в точке настройке безопасности,
 * в бине типа SecurityFilterChain
 */
@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint, AccessDeniedHandler {

    /**
     * Этот метод будет срабатывать каждый раз, когда неаутентифицированный пользователь
     * запрашивает защищенный HTTP-ресурс и выдается исключение AuthenticationException.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("detail", authException.getMessage());
        body.put("instance", request.getServletPath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

    /**
     * Этот метод будет срабатывать всякий раз, когда аутентифицированный пользователь захочет
     * получить доступ к защищенному ресурсу с ролью, которой у него нет
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("Unauthorized error: {}", accessDeniedException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("detail", accessDeniedException.getMessage());
        body.put("instance", request.getServletPath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
