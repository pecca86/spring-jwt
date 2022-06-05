package com.example.springjwt.api;


import com.example.springjwt.domain.ApplicationUser;
import com.example.springjwt.domain.UserRole;
import com.example.springjwt.service.ApplicationUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final ApplicationUserService applicationUserService;

    @GetMapping()
    public ResponseEntity<List<ApplicationUser>> getUsers() {
        return ResponseEntity.ok().body(
                applicationUserService.getApplicationUsers()
        );
    }

    @PostMapping()
    public ResponseEntity<ApplicationUser> saveUser(@RequestBody ApplicationUser applicationUser) {

        // Set response upon successful creation
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users").toUriString());

        return ResponseEntity.created(uri).body(
                applicationUserService.saveApplicationUser(applicationUser)
        );
    }

    @PostMapping("role")
    public ResponseEntity<UserRole> saveUserRole(@RequestBody UserRole userRole) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/role").toUriString());

        return ResponseEntity.created(uri).body(
                applicationUserService.saveUserRole(userRole)
        );
    }

    //@RequestBody RoleToUserForm form, form.getUsername;getRoleName
    @PostMapping("role/{username}")
    public ResponseEntity<?> addRoleToUser(@RequestParam("username") String username, @RequestBody UserRole userRole) {
        ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);

        applicationUserService.addRoleToUser(username, userRole.getName());

        // Since addRoleToUser returns void
        return ResponseEntity.ok().build();
    }

}

@Data
class  RoleToUserForm {
    private String username;
    private String roleName;
}
