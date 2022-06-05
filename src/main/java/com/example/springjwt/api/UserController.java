package com.example.springjwt.api;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springjwt.domain.ApplicationUser;
import com.example.springjwt.domain.UserRole;
import com.example.springjwt.service.ApplicationUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
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

    // For automatically refreshing an expired access token, using the refresh token
    @GetMapping("token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Check for header with authorization
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            try {
                String refreshToken = authorizationHeader.replace("Bearer ", "");
                Algorithm algorithm = Algorithm.HMAC256("secretkeysecretkeysecretkeysecretkey".getBytes());

                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);

                String username = decodedJWT.getSubject();
                ApplicationUser applicationUser = applicationUserService.getApplicationUser(username);

                // CREATE NEW TOKEN (DUPLICATED CODE!)
                String accessToken = JWT.create()
                        .withSubject(applicationUser.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", applicationUser.getUserRoles()
                                .stream()
                                .map(UserRole::getName)
                                .collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                // Put the tokens inside to response body in JSON format
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Could not validate user token!");
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);

            }

        } else {
            throw new RuntimeException("Refresh token missing");
        }

    }
}

@Data
class  RoleToUserForm {
    private String username;
    private String roleName;
}
