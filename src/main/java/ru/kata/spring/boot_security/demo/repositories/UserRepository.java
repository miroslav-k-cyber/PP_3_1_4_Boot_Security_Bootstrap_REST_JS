package ru.kata.spring.boot_security.demo.repositories;

import ru.kata.spring.boot_security.demo.models.User;
import java.util.List;

public interface UserRepository {
    List<User> findAll();
    
    User findById(Long id);
    
    User findByEmail(String email);
    
    void save(User user);
    
    void deleteById(Long id);
    
boolean existsByEmail (String email);
}
