package ru.kata.spring.boot_security.demo.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.models.User;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<User> findAll() {
        // JOIN FETCH подгружает роли сразу, чтобы не было LazyInitializationException
        return entityManager.createQuery("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class).getResultList();
    }
    
    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }
    
    @Override
    public User findByEmail(String email) {
        // LEFT JOIN FETCH подгружает роли сразу, чтобы Spring Security мог их прочитать
        List<User> users = entityManager.createQuery(
                        "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
    
    @Override
    public void save(User user) {
        if (user.getId() == null) entityManager.persist(user);
        else entityManager.merge(user);
    }
    
    @Override
    public void deleteById(Long id) {
        User user = findById(id);
        if (user != null) entityManager.remove(user);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}