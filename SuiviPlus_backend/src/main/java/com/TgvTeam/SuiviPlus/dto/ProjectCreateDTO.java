package com.TgvTeam.SuiviPlus.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectCreateDTO {

    private String nom;
    private String description;

    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
}