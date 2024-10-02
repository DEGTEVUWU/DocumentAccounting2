package com.ivan_degtev.documentaccounting2.config.security;

import com.ivan_degtev.documentaccounting2.config.security.jwt.AuthEntryPointJwt;
import com.ivan_degtev.documentaccounting2.config.security.jwt.AuthTokenFilter;
import com.ivan_degtev.documentaccounting2.service.impl.UserServiceImpl;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
@EnableTransactionManagement
public class WebSecurityConfig {

    private UserServiceImpl userService;
    private AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    /**
     * Используется, чтоб создать бин AuthTokenFilter - основной фильтр для http-запросов, а именно здесь происходит внедрение
     * зависимостей в AuthTokenFilter, которые нужны ему для выполнения бизнес-логики
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userService);
    }

    /**
     * Провайдер аутентификации - бин встраивается в основной фильтр  filterChain и
     * используется для аутентификации пользователей на основе данных из базы данных, которые управляются через UserDetailsService.
     * В объект DaoAuthenticationProvider внедряется репозиторий, из которого берутся данные для проверки и бин по дешифровки
     * паролей для корректного сравнивания
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager - стандартный менеджер аутентификации, работает под капотом в секьюрити, нужно для создания внедрить
     * AuthenticationConfiguration —  класс, который предоставляет конфигурацию, он является частью  Security
     * и управляется автоматически
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Бин создает шифровальщик BCryptPasswordEncoder, который исп. при регистрации для шифрования пароля
     * и в др. логике работы с юзером
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Основной бин для настроек безопасности приложения.
     * Там указываются правила по выбрасыванию исключений(кастомные, созданные ранее), настраиваются матчеры -
     * для доступа на определённые ресурсы приложения в зависимости от аутентификации,
     * используется провайдер аутентификации authenticationProvider, определённый ранее, для корректной обработки данных юзеров.
     * строка addFilterBefore добавляет пользовательский фильтр доступа authenticationJwtTokenFilter ранее и дает ему приоритет
     * перед стандартным фильтром Security UsernamePasswordAuthenticationFilter. Если будет выполнена аутентификация по кастомному
     * фильтру, библиотечный будет работать с уже настроенной аутентификацией
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000",
                                "http://46.148.229.205",
                                "https://46.148.229.205",
                                "http://da-korchel-ivan.ru",
                                "https://da-korchel-ivan.ru")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}