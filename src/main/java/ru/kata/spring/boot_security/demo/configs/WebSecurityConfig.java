package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final SuccessUserHandler successUserHandler;
    private final UserDetailsService userDetailsService;
    
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserDetailsService userDetailsService) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Отключаем CSRF для фоновых запросов
                .csrf(csrf -> csrf.disable())
                
                // 2. Настраиваем патрули на URL
                .authorizeHttpRequests(auth -> auth
                        // РАЗРЕШАЕМ ВСЕ СТАТИЧЕСКИЕ РЕСУРСЫ (CSS, JS, Webjars, Картинки)
                        .requestMatchers(org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/static/static/**", "/css/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/index").permitAll()
                        
                        // Правила для REST-контроллеров бэкенда
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user").hasAnyRole("USER", "ADMIN")
                        
                        // Правила для HTML-страниц
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user").hasAnyRole("USER", "ADMIN")
                        
                        .anyRequest().authenticated()
                )
                // 3. Форма логина
                .formLogin(form -> form
                        .usernameParameter("username")
                        .successHandler(successUserHandler)
                        .permitAll()
                )
                // 4. Логаут
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .authenticationProvider(daoAuthenticationProvider(userDetailsService))
                .build();
    }
    
    
    // 2 (Провайдер базы данных)
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService); // Передаем сервис для работы с БД
        return provider;
    }
    
    // 3 (Шифровальщик паролей)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

