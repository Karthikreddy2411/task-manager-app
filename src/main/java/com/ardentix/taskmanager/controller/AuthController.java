package com.ardentix.taskmanager.controller;

import com.ardentix.taskmanager.model.User;
import com.ardentix.taskmanager.repository.UserRepository;
import com.ardentix.taskmanager.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

  @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    if (userRepo.findByUsername(user.getUsername()).isPresent()) {
        return ResponseEntity
                .status(409)
                .body(Map.of("error", "Username already exists"));
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepo.save(user);

    return ResponseEntity.ok(Map.of("message", "Registered"));
}


@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    User dbUser = userRepo.findByUsername(user.getUsername())
            .orElse(null);

    if (dbUser == null) {
        return ResponseEntity
                .status(404)
                .body(Map.of("error", "User not found"));
    }

    if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
        return ResponseEntity
                .status(401)
                .body(Map.of("error", "Wrong password"));
    }

    String token = jwtUtil.generateToken(dbUser.getUsername());
    return ResponseEntity.ok(Map.of("token", token));
}

}
