package com.municipality.smart.service;

import com.municipality.smart.dto.AuthRequest;
import com.municipality.smart.dto.AuthResponse;
import com.municipality.smart.dto.RegisterRequest;
import com.municipality.smart.entity.User;
import com.municipality.smart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + req.getEmail());
        }

        String role = req.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "CITIZEN";
        } else {
            role = role.trim().toUpperCase();
        }

        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(req.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        user.setPhone(req.getPhone() != null ? req.getPhone().trim() : null);

        User saved = userRepository.save(user);
        String mockToken = UUID.randomUUID().toString();
        return new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), saved.getPhone(), mockToken);
    }

    public AuthResponse authenticate(AuthRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.getEmail().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String mockToken = UUID.randomUUID().toString();
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getPhone(), mockToken);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
}
