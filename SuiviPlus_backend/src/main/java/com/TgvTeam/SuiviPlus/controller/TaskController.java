package com.TgvTeam.SuiviPlus.controller;

import com.TgvTeam.SuiviPlus.dto.TaskRequestDTO;
import com.TgvTeam.SuiviPlus.dto.TaskResponseDTO;
import com.TgvTeam.SuiviPlus.enums.TaskStatus;
import com.TgvTeam.SuiviPlus.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // =========================
    // GET ALL TASKS
    // CORRECTION 1 : Task → TaskResponseDTO
    // =========================
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    // =========================
    // GET TASK BY ID
    // CORRECTION 1 : Task → TaskResponseDTO
    // =========================
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        return ResponseEntity.ok(taskService.getTaskById(projectId, taskId));
    }

    // =========================
    // CREATE TASK
    // CORRECTION 1 : Task → TaskResponseDTO
    // CORRECTION 2 : @RequestBody Task → @RequestBody TaskRequestDTO
    // =========================
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @PathVariable UUID projectId,
            @RequestBody TaskRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, dto));
    }

    // =========================
    // UPDATE TASK
    // CORRECTION 1 : Task → TaskResponseDTO
    // CORRECTION 2 : @RequestBody Task → @RequestBody TaskRequestDTO
    // + appel réel au service (était un placeholder vide)
    // =========================
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestBody TaskRequestDTO dto
    ) {
        return ResponseEntity.ok(taskService.updateTask(projectId, taskId, dto));
    }

    // =========================
    // DELETE TASK
    // + appel réel au service (était un placeholder vide)
    // =========================
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ) {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // CHANGE STATUS
    // CORRECTION 3 : @RequestParam → @RequestBody
    // Le statut arrive en JSON : { "status": "EN_COURS" }
    // =========================
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> changeStatus(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestBody Map<String, String> body
    ) {
        TaskStatus status = TaskStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(taskService.changeTaskStatus(taskId, status));
    }

    // =========================
    // ASSIGN TASK
    // CORRECTION 4 : @RequestParam → @RequestBody
    // L'userId arrive en JSON : { "userId": "uuid..." }
    // =========================
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponseDTO> assignTask(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestBody Map<String, UUID> body
    ) {
        UUID userId = body.get("userId");
        return ResponseEntity.ok(taskService.assignTask(projectId, taskId, userId));
    }
}