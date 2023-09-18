package com.suutich.systems.repository;

import com.suutich.systems.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByName(String name);
    Boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "roles") // 'authorities' 대신 'roles'로 수정
    User findOneWithAuthoritiesByEmail(String email);
}