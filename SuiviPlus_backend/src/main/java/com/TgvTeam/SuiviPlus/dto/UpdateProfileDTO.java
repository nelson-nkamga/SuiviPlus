package com.TgvTeam.SuiviPlus.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String nom;
    private String prenom;
    private String password;
}