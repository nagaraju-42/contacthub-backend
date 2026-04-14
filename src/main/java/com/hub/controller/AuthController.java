// backend/src/main/java/com/hub/controller/AuthController.java
package com.hub.controller;

import com.hub.dto.AuthRequestDTO;
import com.hub.dto.AuthResponseDTO;
import com.hub.entity.User;
import com.hub.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody AuthRequestDTO request) {
        // Validate input
        if (request.getName() == null || request.getName().isBlank() ||
            request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponseDTO(null, "Name, email, and password are required"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponseDTO(null, "Email already exists"));
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(bCryptPasswordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        // Generate JWT
        String token = generateJwt(user.getId().toString());
        return ResponseEntity.ok(new AuthResponseDTO(token, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        // Load user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(null, "Invalid email or password"));
        }

        User user = userOpt.get();

        // Verify password
        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(null, "Invalid email or password"));
        }

        // Generate JWT
        String token = generateJwt(user.getId().toString());
        return ResponseEntity.ok(new AuthResponseDTO(token, "Login successful"));
    }

    private String generateJwt(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}
