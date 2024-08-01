package com.ivan_degtev.documentaccounting2.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

/**
 * Утилитный класс для работы и настройки секьюрити
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * Секретный ключ, используемый для подписывания и проверки JWT
     */
    @Value("${document_accounting.jwtSecret}")
    private String jwtSecret;

    /**
     *  Время истечения JWT в миллисекундах
     */
    @Value("${document_accounting.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Имя файла cookie, в котором хранится JWT.
     */
    @Value("${document_accounting.jwtCookieName}")
    private String jwtCookieName;

    /**
     *  получить JWT из файлов cookie по имени файла cookie
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    /**
     * генерируем файл cookie, содержащий JWT(который генерится другим методом generateTokenFromUsername),
     * на основе имени пользователя, даты, срока действия и секрета
     */
    public ResponseCookie generateJwtCookie(String username) {
        String jwt = generateTokenFromUsername(username);
        return ResponseCookie
                .from(jwtCookieName, jwt)
                .path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(true)
                .build();
    }

    /**
     * возвращаем файл cookie с нулевым значением (используется для очистки файла cookie)
     */
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie
                .from(jwtCookieName, null)
                .path("/api")
                .build();
    }

    /**
     * Создает парсер JWT с использованием секретного ключа (key).
     * Парсит JWT и извлекает тело (Claims).
     * Возвращает значение subject из тела JWT, которое является именем пользователя.
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody()
                .getSubject();
    }

    /**
     * Создает и возвращет ключ для подписывания и проверки JWT
     * Декодирует jwtSecret из Base64, создает HMAC ключ (Key) с использованием декодированного значения.
     * @return Возвращает созданный ключ.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Проверяем JWT с помощью секрета на корректность и валидность.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Генерирует JWT на основе имени пользователя.
     * Устанавливает subject JWT (имя пользователя), дату выдачи JWT, дату истечения JWT на основе текущей даты и jwtExpirationMs.
     * Подписывает JWT с использованием секретного ключа (key) и алгоритма HMAC SHA-256.
     * @return Возвращает сгенерированный JWT.
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}
