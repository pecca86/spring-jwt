package com.example.springjwt.service;

import com.example.springjwt.domain.ApplicationUser;
import com.example.springjwt.domain.UserRole;
import com.example.springjwt.repo.ApplicationUserRepo;
import com.example.springjwt.repo.UserRoleRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ApplicationUserServiceImpl implements ApplicationUserService {
    private final ApplicationUserRepo applicationUserRepo;
    private final UserRoleRepo userRoleRepo;

    @Autowired
    public ApplicationUserServiceImpl(ApplicationUserRepo applicationUserRepo, UserRoleRepo userRoleRepo) {
        this.applicationUserRepo = applicationUserRepo;
        this.userRoleRepo = userRoleRepo;
    }

    @Override
    public ApplicationUser saveApplicationUser(ApplicationUser applicationUser) {
        return applicationUserRepo.save(applicationUser);
    }

    @Override
    public UserRole saveUserRole(UserRole userRole) {
        return userRoleRepo.save(userRole);
    }

    @Override
    public void addRoleToUser(String username, String userRoleName) {
        ApplicationUser applicationUser = applicationUserRepo.findByUsername(username);
        UserRole userRole = userRoleRepo.findByName(userRoleName);
        applicationUser.getUserRoles().add(userRole);
        // applicationUserRepo.save(applicationUser); << THIS IS NOT NEEDED SINCE WE HAVE THE @TRANSACTIONAL ANNOTATION!
    }

    @Override
    public ApplicationUser getApplicationUser(String username) {
        return null;
    }

    @Override
    public List<ApplicationUser> getApplicationUsers() {
        return null;
    }
}
