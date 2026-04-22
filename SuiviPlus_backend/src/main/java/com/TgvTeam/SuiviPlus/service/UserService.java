package com.TgvTeam.SuiviPlus.service;

import com.TgvTeam.SuiviPlus.entity.User;
import com.TgvTeam.SuiviPlus.enums.Role;
import com.TgvTeam.SuiviPlus.exception.BadRequestException;
import com.TgvTeam.SuiviPlus.exception.ForbiddenException;
import com.TgvTeam.SuiviPlus.exception.ResourceNotFoundException;
import com.TgvTeam.SuiviPlus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getMyProfile() {
        return getCurrentUser();
    }

    public User updateMyProfile(User updated) {
        User user = getCurrentUser();

        if (updated.getNom() != null)
            user.setNom(updated.getNom());

        if (updated.getPrenom() != null)
            user.setPrenom(updated.getPrenom());

        if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updated.getPassword()));
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        User user = getCurrentUser();

        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        return userRepository.findAll();
    }

    public User changeUserRole(UUID userId, Role newRole) {
        User admin = getCurrentUser();

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        if (admin.getId().equals(userId)) {
            throw new BadRequestException("Impossible de modifier son propre rôle");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
