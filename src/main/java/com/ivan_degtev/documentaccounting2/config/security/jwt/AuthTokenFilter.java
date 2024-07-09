package com.ivan_degtev.documentaccounting2.config.security.jwt;

import com.ivan_degtev.documentaccounting2.service.impl.UserServiceImpl;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserServiceImpl userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            logger.info("получил jwt из куки, JWT: {}", jwt);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                logger.info("зашел в условие ненулевого токена и... жду юзернейма из утила по jwt");
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                logger.info("получил юзернейм");
                UserDetails userDetails = userService.loadUserByUsername(username);
                logger.info("получил юзер детейлс по юзернейме");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("получил аутентификацию" + authentication.toString());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("засунул аутент в контекст");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtFromCookies(request);
    }
}
