package com.TgvTeam.SuiviPlus.dto;

import com.TgvTeam.SuiviPlus.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskResponseDTO {

    private UUID id;
    private String titre;
    private String description;
    private TaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime dueDate;

    private UUID projectId;
    private UUID createdBy;
}