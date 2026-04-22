package com.TgvTeam.SuiviPlus.dto;

import com.TgvTeam.SuiviPlus.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskRequestDTO {

    private String title;
    private String description;
    private TaskStatus status;

    private LocalDate dueDate;
}