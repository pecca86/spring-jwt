package com.example.springjwt.service;

import com.example.springjwt.domain.ApplicationUser;
import com.example.springjwt.domain.UserRole;

import java.util.List;

public interface ApplicationUserService {

    ApplicationUser saveApplicationUser(ApplicationUser applicationUser);
    UserRole saveUserRole(UserRole userRole);

    // This assumes unique usernames
    void addRoleToUser(String username, String userRoleName);
    ApplicationUser getApplicationUser(String username);

    // In the real world you would use pagination
    List<ApplicationUser> getApplicationUsers();
}
