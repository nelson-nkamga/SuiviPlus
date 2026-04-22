package com.TgvTeam.SuiviPlus.dto;

import com.TgvTeam.SuiviPlus.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponseDTO {
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
}