package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserService userService;
    
    public AdminRestController(UserService userService) {
        this.userService = userService;
    }
    
    // 1. Получить всех
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    // 2. Получить одного  для модалки
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto requestDto) {
        try {
            User user = new User();
            user.setFirstName(requestDto.getFirstName());
            user.setLastName(requestDto.getLastName());
            user.setAge(requestDto.getAge());
            user.setEmail(requestDto.getEmail());
            user.setPassword(requestDto.getPassword());
            userService.saveUser(user, requestDto.getRoleIds());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (IllegalArgumentException e) {
            // Если email уже существует — возвращаем 400 с сообщением
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 4. Обновить
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequestDto requestDto) {
        User user = new User();
        user.setId(id);
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setAge(requestDto.getAge());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        userService.updateUser(user, requestDto.getRoleIds());
        return ResponseEntity.ok(user);
    }
    
    // 5. Удалить
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}