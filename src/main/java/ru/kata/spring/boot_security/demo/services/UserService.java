package ru.kata.spring.boot_security.demo.services;

import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    
    User getUserById(Long id);
    
    void saveUser(User user, List<Long> roleIds); // Принимает ID ролей из контроллера
    
    void updateUser(User user, List<Long> roleIds);
    
    void deleteUser(Long id);
}
