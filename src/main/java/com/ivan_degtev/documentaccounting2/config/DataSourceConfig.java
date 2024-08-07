package com.ivan_degtev.documentaccounting2.config;

import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Конфиг бин, создает DataSourceTransactionManager для работы с прямым доступом к JDBC, минуя JPA
 * (нужно пока только для обёртки метода, который ссылается на асинхронный метод по работе с внешним API)
 * Сделан для демонстрации - идея в том, чтоб предотвратить потерю других данных, при отсутсвии ответа от внешнего сервера
 * Также добавлен бин с конфигом liquibase - для возможной в будушем работы с БД через liquibase прямо в коде
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Autowired
    private DataSource dataSource;

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "dataSourceTransactionManager")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }
}
