package com.ivan_degtev.documentaccounting2.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.print.attribute.standard.JobKOctets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${document_accounting.jwtSecret}")
    private String jwtSecret;

    @Value("${document_accounting.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${document_accounting.jwtCookieName}")
    private String jwtCookieName;

    //получить JWT из файлов cookie по имени файла cookie
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    //генерируем файл cookie, содержащий JWT, на основе имени пользователя, даты, срока действия и секрета
    public ResponseCookie generateJwtCookie(String username) {
        String jwt = generateTokenFromUsername(username);
        return ResponseCookie.from(jwtCookieName, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    //возвращаем файл cookie с нулевым значением (используется для очистки файла cookie)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, null).path("/api").build();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //проверяем JWT с помощью секрета
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}
