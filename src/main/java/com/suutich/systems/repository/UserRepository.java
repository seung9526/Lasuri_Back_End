package com.suutich.systems.repository;

import com.suutich.systems.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByName(String name);
    Boolean existsByEmail(String email);
}