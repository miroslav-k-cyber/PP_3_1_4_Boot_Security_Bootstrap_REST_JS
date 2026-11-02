package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService; // Общаемся с ролями только через их сервис
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional
    public void saveUser(User user, List<Long> roleIds) {
        // ПРОВЕРКА: если email уже существует — выбрасываем исключение
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует: " + user.getEmail());
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Role> roles = roleService.findRolesByIds(roleIds);
            user.setRoles(new HashSet<>(roles));
        }
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void updateUser(User user, List<Long> roleIds) {
        User existingUser = userRepository.findById(user.getId());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Role> roles = roleService.findRolesByIds(roleIds);
            user.setRoles(new HashSet<>(roles));
        } else {
            user.setRoles(existingUser.getRoles());
        }
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
