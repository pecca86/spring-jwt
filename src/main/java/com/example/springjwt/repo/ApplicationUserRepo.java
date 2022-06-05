package com.example.springjwt.repo;

import com.example.springjwt.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepo extends JpaRepository<ApplicationUser, Long> {

    // Spring JPA automatically creates a query for this
    ApplicationUser findByUsername(String username);
}
