package com.TgvTeam.SuiviPlus.controller;

import com.TgvTeam.SuiviPlus.dto.UpdateProfileDTO;
import com.TgvTeam.SuiviPlus.dto.UpdateRoleDTO;
import com.TgvTeam.SuiviPlus.dto.UserResponseDTO;
import com.TgvTeam.SuiviPlus.entity.User;
import com.TgvTeam.SuiviPlus.mapper.UserMapper;
import com.TgvTeam.SuiviPlus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        return ResponseEntity.ok(
                UserMapper.toDTO(userService.getMyProfile())
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(@RequestBody UpdateProfileDTO dto) {

        User updated = new User();
        updated.setNom(dto.getNom());
        updated.setPrenom(dto.getPrenom());
        updated.setPassword(dto.getPassword());

        return ResponseEntity.ok(
                UserMapper.toDTO(userService.updateMyProfile(updated))
        );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
                        .stream()
                        .map(UserMapper::toDTO)
                        .toList()
        );
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> changeUserRole(
            @PathVariable UUID id,
            @RequestBody UpdateRoleDTO dto
    ) {
        return ResponseEntity.ok(
                UserMapper.toDTO(userService.changeUserRole(id, dto.getRole()))
        );
    }
}