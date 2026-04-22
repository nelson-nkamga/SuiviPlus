package com.TgvTeam.SuiviPlus.service;

import com.TgvTeam.SuiviPlus.dto.TaskRequestDTO;
import com.TgvTeam.SuiviPlus.dto.TaskResponseDTO;
import com.TgvTeam.SuiviPlus.entity.*;
import com.TgvTeam.SuiviPlus.entity.composite.UserTaskId;
import com.TgvTeam.SuiviPlus.enums.TaskStatus;
import com.TgvTeam.SuiviPlus.exception.BadRequestException;
import com.TgvTeam.SuiviPlus.exception.ForbiddenException;
import com.TgvTeam.SuiviPlus.exception.ResourceNotFoundException;
import com.TgvTeam.SuiviPlus.mapper.TaskMapper;
import com.TgvTeam.SuiviPlus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserTaskRepository userTaskRepository;
    private final UserRepository userRepository;

    // =========================
    // GET ALL
    // =========================
    public List<TaskResponseDTO> getTasksByProject(UUID projectId) {

        checkMembership(projectId);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    // =========================
    // GET BY ID
    // =========================
    public TaskResponseDTO getTaskById(UUID projectId, UUID taskId) {

        checkMembership(projectId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Tâche invalide pour ce projet");
        }

        return TaskMapper.toDTO(task);
    }

    // =========================
    // CREATE
    // =========================
    public TaskResponseDTO createTask(UUID projectId, TaskRequestDTO dto) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        checkMembership(projectId);

        Task task = new Task();
        task.setTitre(dto.getTitle());
        task.setDescription(dto.getDescription());

        task.setStatus(dto.getStatus() != null ? dto.getStatus() : TaskStatus.A_FAIRE);

        task.setDateEcheance(dto.getDueDate());

        task.setProject(project);
        task.setCreateur(getCurrentUser());

        Task saved = taskRepository.save(task);

        return TaskMapper.toDTO(saved);
    }

    // =========================
    // ASSIGN TASK
    // =========================
    public TaskResponseDTO assignTask(UUID projectId, UUID taskId, UUID userId) {

        checkMembership(projectId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Tâche invalide pour ce projet");
        }

        validateMember(projectId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        boolean alreadyAssigned = userTaskRepository
                .existsById(new UserTaskId(userId, taskId));

        if (alreadyAssigned) {
            throw new BadRequestException("Déjà assigné");
        }

        UserTask userTask = new UserTask();
        userTask.setId(new UserTaskId(userId, taskId));
        userTask.setTask(task);
        userTask.setAssignee(user);
        userTask.setAssignePar(getCurrentUser());
        userTask.setAssigneLe(LocalDateTime.now());
        userTask.setTypeAssignation("ASSIGNEE");

        userTaskRepository.save(userTask);

        return TaskMapper.toDTO(task);
    }

    // =========================
    // STATUS
    // =========================
    public TaskResponseDTO changeTaskStatus(UUID taskId, TaskStatus status) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        checkMembership(task.getProject().getId());

        task.setStatus(status);

        return TaskMapper.toDTO(taskRepository.save(task));
    }

    // =========================
    // SECURITY
    // =========================
    private void checkMembership(UUID projectId) {

        User user = getCurrentUser();

        boolean isMember = userProjectRepository
                .existsByProjectIdAndUserId(projectId, user.getId());

        if (!isMember && !isProjectOwner(projectId, user.getId())) {
            throw new ForbiddenException("Accès interdit");
        }
    }

    private void validateMember(UUID projectId, UUID userId) {

        boolean isMember = userProjectRepository
                .existsByProjectIdAndUserId(projectId, userId);

        if (!isMember) {
            throw new BadRequestException("Utilisateur non membre");
        }
    }

    private boolean isProjectOwner(UUID projectId, UUID userId) {
        return projectRepository.existsByIdAndCreateurId(projectId, userId);
    }

    private User getCurrentUser() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new ForbiddenException("Non authentifié");
    }

    // =========================
// UPDATE
// =========================
    public TaskResponseDTO updateTask(UUID projectId, UUID taskId, TaskRequestDTO dto) {

        checkMembership(projectId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Tâche invalide pour ce projet");
        }

        // Règle métier : seul le créateur du projet ou admin peut modifier
        User current = getCurrentUser();
        boolean isOwner = isProjectOwner(projectId, current.getId());
        if (!isOwner) {
            throw new ForbiddenException("Seul le créateur du projet peut modifier une tâche");
        }

        if (dto.getTitle() != null)       task.setTitre(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getDueDate() != null)     task.setDateEcheance(dto.getDueDate());
        if (dto.getStatus() != null)      task.setStatus(dto.getStatus());

        return TaskMapper.toDTO(taskRepository.save(task));
    }

    // =========================
// DELETE
// =========================
    public void deleteTask(UUID projectId, UUID taskId) {

        checkMembership(projectId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));

        if (!task.getProject().getId().equals(projectId)) {
            throw new ForbiddenException("Tâche invalide pour ce projet");
        }

        // Règle métier : seul le créateur du projet ou admin peut supprimer
        User current = getCurrentUser();
        if (!isProjectOwner(projectId, current.getId())) {
            throw new ForbiddenException("Seul le créateur du projet peut supprimer une tâche");
        }

        taskRepository.delete(task);
    }
}