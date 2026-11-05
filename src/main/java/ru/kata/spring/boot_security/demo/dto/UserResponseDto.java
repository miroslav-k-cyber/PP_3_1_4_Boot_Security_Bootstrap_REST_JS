package ru.kata.spring.boot_security.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private Set<RoleDto> roles;
    
    // Вложенный DTO для ролей
    @Getter
    @Setter
    @NoArgsConstructor
    public static class RoleDto {
        private Long id;
        private String name;
    }
}