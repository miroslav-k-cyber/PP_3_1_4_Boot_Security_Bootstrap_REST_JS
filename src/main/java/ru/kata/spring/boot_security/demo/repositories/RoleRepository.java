package ru.kata.spring.boot_security.demo.repositories;

import ru.kata.spring.boot_security.demo.models.Role;
import java.util.List;

public interface RoleRepository {
    List<Role> findAll();
    List<Role> findAllById(List<Long> ids);
    void save(Role role);
}