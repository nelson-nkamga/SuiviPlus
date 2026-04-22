package com.TgvTeam.SuiviPlus.mapper;

import com.TgvTeam.SuiviPlus.dto.UserResponseDTO;
import com.TgvTeam.SuiviPlus.entity.User;

public class UserMapper {

    public static UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}