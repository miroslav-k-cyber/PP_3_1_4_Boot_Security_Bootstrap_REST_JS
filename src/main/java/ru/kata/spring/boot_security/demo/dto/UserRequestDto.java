package ru.kata.spring.boot_security.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private String password;
    private List<Long> roleIds;
}