package com.ivan_degtev.documentaccounting2.component;


import com.ivan_degtev.documentaccounting2.config.security.jwt.AuthEntryPointJwt;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@Slf4j
public class LogoutOnStartup {

    private static final Logger logger = LoggerFactory.getLogger(LogoutOnStartup.class);
    private HttpServletResponse response;
    private JwtUtils jwtUtils;

    @PostConstruct
    public void logoutUserOnStartup() {
        logger.info("зашли в метод после конструктора");
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie(); // Метод для создания "чистой" куки
        logger.info("очистил куку");
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        logger.info("добавил в ответ хедер и пустую куку");
    }
}