package com.TgvTeam.SuiviPlus.dto;

import com.TgvTeam.SuiviPlus.enums.ProjectStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectUpdateDTO {

    private String nom;
    private String description;

    private ProjectStatus statut;

    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private LocalDate dateFinReelle;
}