package com.ivan_degtev.documentaccounting2.config.security.jwt;

import com.ivan_degtev.documentaccounting2.service.impl.UserServiceImpl;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Является фильтром, который обрабатывает каждый HTTP-запрос, проходящий через приложение. Инициализируется в
 * конфигруационном классе секьюрити WebSecurityConfig.
 * Выполняет проверку JWT для аутентификации пользователя.
 */
@Slf4j
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final  UserServiceImpl userService;

    /**
     * Основной метод фильтра, который выполняется для каждого запроса.
     * Сначала извелкается jwt из куки запроса, далее пробуем найти аутентификацию.
     * Ищется юзер по jwt через утилитарный метод, объект UserDetails с данными текущего юзера(в контексте секьюрити - доступы,
     * пароли и пр.),
     * Далее создается объект аутентификации, куда закидываются данные юзера и этот объект помещается в контекст безопасности
     * SecurityContextHolder
     * Если в процессе аутентификации возникнет ошибка, она будет залогирована - когда юзер не аутентифицирован
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException
    {
        try {
            String jwt = parseJwt(request);
            log.info("получил jwt из куки, JWT: {}", jwt);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                log.info("зашел в условие ненулевого токена и... жду юзернейма из утила по jwt");
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                log.info("получил юзернейм");
                UserDetails userDetails = userService.loadUserByUsername(username);
                log.info("получил юзер детейлс по юзернейме");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("получил аутентификацию {}", authentication.toString());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("засунул аутент в контекст");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Утилитарный метод для поиска jwt-токена по принятому  HTTP-запросу
     */
    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtFromCookies(request);
    }
}
