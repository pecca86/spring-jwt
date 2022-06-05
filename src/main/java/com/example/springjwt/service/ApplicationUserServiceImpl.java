package com.example.springjwt.service;

import com.example.springjwt.domain.ApplicationUser;
import com.example.springjwt.domain.UserRole;
import com.example.springjwt.repo.ApplicationUserRepo;
import com.example.springjwt.repo.UserRoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ApplicationUserServiceImpl implements ApplicationUserService, UserDetailsService {
    private final ApplicationUserRepo applicationUserRepo;
    private final UserRoleRepo userRoleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationUserServiceImpl(ApplicationUserRepo applicationUserRepo, UserRoleRepo userRoleRepo, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        log.info("{} connected!", this.getClass().getName());
        this.applicationUserRepo = applicationUserRepo;
        this.userRoleRepo = userRoleRepo;
    }

    @Override
    public ApplicationUser saveApplicationUser(ApplicationUser applicationUser) {
        log.info("Saving user {}", applicationUser);
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        return applicationUserRepo.save(applicationUser);
    }

    @Override
    public UserRole saveUserRole(UserRole userRole) {
        log.info("Saving user role {}", userRole);
        return userRoleRepo.save(userRole);
    }

    @Override
    public void addRoleToUser(String username, String userRoleName) {
        log.info("Adding role {} to user {}", userRoleName, username);
        ApplicationUser applicationUser = applicationUserRepo.findByUsername(username);
        UserRole userRole = userRoleRepo.findByName(userRoleName);
        applicationUser.getUserRoles().add(userRole);
        // applicationUserRepo.save(applicationUser); << THIS IS NOT NEEDED SINCE WE HAVE THE @TRANSACTIONAL ANNOTATION!
    }

    @Override
    public ApplicationUser getApplicationUser(String username) {
        log.info("Retrieving user {}", username);
        return applicationUserRepo.findByUsername(username);
    }

    @Override
    public List<ApplicationUser> getApplicationUsers() {
        log.info("Retrieving all users...");
        return applicationUserRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = applicationUserRepo.findByUsername(username);
        if (applicationUser == null) {
            log.error("User not found!");
            throw new UsernameNotFoundException("User not found inside the database");
        } else {
            log.info("User found in database");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        applicationUser.getUserRoles()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        // User is from spring security
        return new User(applicationUser.getUsername(), applicationUser.getPassword(), authorities);
    }
}
