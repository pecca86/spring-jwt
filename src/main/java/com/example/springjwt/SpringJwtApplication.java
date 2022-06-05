package com.example.springjwt;

import com.example.springjwt.domain.ApplicationUser;
import com.example.springjwt.domain.UserRole;
import com.example.springjwt.service.ApplicationUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class SpringJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJwtApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner commandLineRunner(ApplicationUserService applicationUserService) {
        return args -> {
            applicationUserService.saveUserRole(new UserRole(null, "ROLE_USER"));
            applicationUserService.saveUserRole(new UserRole(null, "ROLE_ADMIN"));
            applicationUserService.saveUserRole(new UserRole(null, "ROLE_MANAGER"));
            applicationUserService.saveUserRole(new UserRole(null, "ROLE_TRAINEE"));

            applicationUserService.saveApplicationUser(new ApplicationUser(null, "Pekka", "pexi", "password", new ArrayList<>()));
            applicationUserService.saveApplicationUser(new ApplicationUser(null, "Gege", "bögen", "password", new ArrayList<>()));
            applicationUserService.saveApplicationUser(new ApplicationUser(null, "Flexi", "flip", "password", new ArrayList<>()));
            applicationUserService.saveApplicationUser(new ApplicationUser(null, "Moscha", "mama", "password", new ArrayList<>()));

            applicationUserService.addRoleToUser("pexi", "ROLE_ADMIN");
            applicationUserService.addRoleToUser("bögen", "ROLE_USER");
            applicationUserService.addRoleToUser("flip", "ROLE_TRAINEE");
            applicationUserService.addRoleToUser("mama", "ROLE_MANAGER");
        };
    }

}
