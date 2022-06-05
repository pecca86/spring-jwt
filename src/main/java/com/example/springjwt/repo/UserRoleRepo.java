package com.example.springjwt.repo;

import com.example.springjwt.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepo extends JpaRepository<UserRole, Long> {

    UserRole findByName(String name);
}
