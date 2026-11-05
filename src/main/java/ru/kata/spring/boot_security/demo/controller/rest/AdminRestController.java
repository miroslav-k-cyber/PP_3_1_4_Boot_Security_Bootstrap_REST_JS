package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserService userService;
    private final RoleService roleService;
    
    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    // Получить все роли для формы создания/редактирования
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
    
    // 1. Получить всех пользователей
    @GetMapping("/users") // Изменено на /users, чтобы соответствовать стандарту REST и разделению урлов
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    // 2. Получить одного пользователя по ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponseDto(user));
    }
    
    // 3. Создать нового пользователя
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto requestDto) {
        try {
            User user = new User();
            user.setFirstName(requestDto.getFirstName());
            user.setLastName(requestDto.getLastName());
            user.setAge(requestDto.getAge());
            user.setEmail(requestDto.getEmail());
            user.setPassword(requestDto.getPassword());
            
            // Сохраняем в бд (сервис должен захэшировать пароль и привязать роли)
            userService.saveUser(user, requestDto.getRoleIds());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 4. Обновить пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto requestDto) {
        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }
        
        existingUser.setFirstName(requestDto.getFirstName());
        existingUser.setLastName(requestDto.getLastName());
        existingUser.setAge(requestDto.getAge());
        existingUser.setEmail(requestDto.getEmail());
        
        // ВАЖНО: Если пароль пришел пустой, оставляем старый хэшированный пароль из БД,
        // чтобы не затереть его пустой строкой. Если новый — передаем его (сервис захэширует).
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            existingUser.setPassword(requestDto.getPassword());
        }
        
        userService.updateUser(existingUser, requestDto.getRoleIds());
        return ResponseEntity.ok(mapToResponseDto(existingUser));
    }
    
    // 5. Удалить пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build(); // Стандарт REST: пустой ответ 200 OK или 204 No Content
    }
    
    // Вспомогательный метод: User -> UserResponseDto (без пароля)
    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAge(user.getAge());
        dto.setEmail(user.getEmail());
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> {
                        UserResponseDto.RoleDto roleDto = new UserResponseDto.RoleDto();
                        roleDto.setId(role.getId());
                        roleDto.setName(role.getName());
                        return roleDto;
                    })
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}
