package ru.kata.spring.boot_security.demo.controller.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    
    @GetMapping
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal User user) {
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