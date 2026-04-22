package com.TgvTeam.SuiviPlus.mapper;

import com.TgvTeam.SuiviPlus.dto.TaskResponseDTO;
import com.TgvTeam.SuiviPlus.entity.Task;

public class TaskMapper {

    public static TaskResponseDTO toDTO(Task task) {

        if (task == null) {
            return null;
        }

        return TaskResponseDTO.builder()
                .id(task.getId())
                .titre(task.getTitre())
                .description(task.getDescription())
                .status(task.getStatus())

                // ton DTO n'a que createdAt et dueDate
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDateEcheance() != null
                        ? task.getDateEcheance().atStartOfDay()
                        : null)

                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .createdBy(task.getCreateur() != null ? task.getCreateur().getId() : null)

                .build();
    }
}
