package ru.kata.spring.boot_security.demo.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;
    
    public DatabaseInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Проверяем, если ролей еще нет в базе — создаем их напрямую через нативный SQL
        Long rolesCount = (Long) entityManager.createQuery("SELECT COUNT(r) FROM Role r").getSingleResult();
        if (rolesCount == 0) {
            entityManager.createNativeQuery("INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN')").executeUpdate();
            entityManager.createNativeQuery("INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER')").executeUpdate();
        }
        // 2. Проверяем, если таблица пользователей пустая — создаем Админа и Пользователя строго по вашему конструктору
        if (userRepository.findAll().isEmpty()) {
            Role adminRole = entityManager.find(Role.class, 1L);
            Role userRole = entityManager.find(Role.class, 2L);
            // Создаем админа (логин: admin@mail.ru, пароль: admin)
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            User admin = new User("Ivan", "Ivanov", "admin@mail.ru", 30, "admin", adminRoles);
            userRepository.save(admin);
            // Создаем обычного юзера (логин: user@mail.ru, пароль: user)
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            User user = new User("Petr", "Petrov", "user@mail.ru", 25, "user", userRoles);
            userRepository.save(user);
        }
    }
}

