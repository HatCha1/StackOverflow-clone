package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically provides methods like save(), findById(), deleteById(), findAll()
    // You can also add custom query methods if needed, like:
    User findByEmail(String email);
}
