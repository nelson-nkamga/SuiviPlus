package com.TgvTeam.SuiviPlus.service;

import com.TgvTeam.SuiviPlus.entity.User;
import com.TgvTeam.SuiviPlus.enums.Role;
import com.TgvTeam.SuiviPlus.repository.UserRepository;
import com.TgvTeam.SuiviPlus.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.MEMBER);

        userRepository.save(user);

        return "Utilisateur créé avec succès";
    }

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe invalide");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}