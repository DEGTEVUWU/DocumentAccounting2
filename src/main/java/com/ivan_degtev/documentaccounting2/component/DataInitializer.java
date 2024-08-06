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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
 * Класс первичной инициализации данных при запуске приложения, создает двух юзеров с ролями администратор и модератор
 * через реализованный метод run интерфейса ApplicationRunner(срабатывает при запуске приложения и выполняет заданную логику)
 * Создание юзеров захардкожено по причине необходимости шифровать пароль, что не получились сделать иным способом,
 * кроме использования PasswordEncoder в коде
 */
@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            createAdminEntity();
            createModeratorEntity();
            log.info("создал админа и модера");
            return null;
        });
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
            adminData.setCreationDate(LocalDate.now());
            log.info("создал джава сущность админа, его юзернейм {}", adminData.getUsername());
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
            moderData.setCreationDate(LocalDate.now());
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
