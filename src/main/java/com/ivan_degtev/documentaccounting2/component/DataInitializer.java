package com.ivan_degtev.documentaccounting2.component;

import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Класс первичной инициализации данных при запуске приложения, создает двух юзеров с ролями администратор и модератор
 * через реализованный метод run интерфейса ApplicationRunner(срабатывает при запуске приложения и выполняет заданную логику)
 */
@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public void run(ApplicationArguments args) {
        createAdminEntity();
        createModeratorEntity();
        log.info("создал админа и модера");
    }

    private void createAdminEntity() {
        Optional<User> firstUser = userRepository.findByEmail("diogteff.ivan@yandex.com");
        if (firstUser.isEmpty()) {
            var adminData = new User();

            String email = "diogteff.ivan@yandex.com";
            String username = "admin";
            String name = "Ivan";
            adminData.setName(name);
            adminData.setUsername(username);
            adminData.setEmail(email);
            adminData.setPassword(passwordEncoder.encode("password"));
            adminData.setRoles(Set.of(createRoleAdmin(), createRoleUser()));
            userRepository.save(adminData);
        }
    }

    private void createModeratorEntity() {
        Optional<User> firstUser = userRepository.findByEmail("example@yandex.com");
        if (firstUser.isEmpty()) {
            var moderData = new User();

            String email = "example@yandex.com";
            String username = "moder";
            String name = "Moder";
            moderData.setName(name);
            moderData.setUsername(username);
            moderData.setEmail(email);
            moderData.setPassword(passwordEncoder.encode("password"));
            moderData.setRoles(Set.of(createRoleModerator()));
            userRepository.save(moderData);
        }
    }

    private Role createRoleUser() {
        return roleService.findByName(RoleEnum.ROLE_USER);
    }
    private Role createRoleAdmin() {
        return roleService.findByName(RoleEnum.ROLE_ADMIN);
    }
    private Role createRoleModerator() {
        return roleService.findByName(RoleEnum.ROLE_MODERATOR);
    }
}
