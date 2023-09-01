package com.suutich.systems.repository;

import com.suutich.systems.model.Role;
import com.suutich.systems.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}