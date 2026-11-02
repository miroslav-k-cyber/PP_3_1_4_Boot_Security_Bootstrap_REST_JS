package ru.kata.spring.boot_security.demo.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    
    @Mock
    private RoleRepository roleRepository;
    
    @InjectMocks
    private RoleServiceImpl roleService;
    
    @Test
    void findRolesByIds_shouldReturnRoles() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<Role> roles = List.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER"));
        
        when(roleRepository.findAllById(ids)).thenReturn(roles);
        
        // when
        List<Role> result = roleService.findRolesByIds(ids);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(roles);
        verify(roleRepository).findAllById(ids);
    }
}