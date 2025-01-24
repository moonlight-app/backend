package ru.moonlightapp.backend.service.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"ru.moonlightapp.backend.core.storage.model", "ru.moonlightapp.backend.service.user.storage.model"})
@EnableJpaRepositories(basePackages = {"ru.moonlightapp.backend.core.storage.repository", "ru.moonlightapp.backend.service.user.storage.repository"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
