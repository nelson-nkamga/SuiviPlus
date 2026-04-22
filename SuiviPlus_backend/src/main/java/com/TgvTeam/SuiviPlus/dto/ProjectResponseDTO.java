package com.TgvTeam.SuiviPlus.dto;

import com.TgvTeam.SuiviPlus.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProjectResponseDTO {

    private UUID id;
    private String nom;
    private String description;
    private ProjectStatus statut;
    private LocalDateTime createdAt;

    // Infos du créateur — on n'expose pas l'objet User entier
    private UUID createurId;
    private String createurNom;
    private String createurPrenom;
}