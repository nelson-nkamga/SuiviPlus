package com.TgvTeam.SuiviPlus.mapper;

import com.TgvTeam.SuiviPlus.dto.ProjectResponseDTO;
import com.TgvTeam.SuiviPlus.entity.Project;

public class ProjectMapper {

    public static ProjectResponseDTO toDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setNom(project.getNom());
        dto.setDescription(project.getDescription());
        dto.setStatut(project.getStatut());
        dto.setCreatedAt(project.getCreatedAt());

        if (project.getCreateur() != null) {
            dto.setCreateurId(project.getCreateur().getId());
            dto.setCreateurNom(project.getCreateur().getNom());
            dto.setCreateurPrenom(project.getCreateur().getPrenom());
        }

        return dto;
    }
}