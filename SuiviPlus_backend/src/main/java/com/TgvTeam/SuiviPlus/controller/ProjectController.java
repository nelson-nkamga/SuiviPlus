package com.TgvTeam.SuiviPlus.controller;

import com.TgvTeam.SuiviPlus.dto.ProjectCreateDTO;
import com.TgvTeam.SuiviPlus.dto.ProjectResponseDTO;
import com.TgvTeam.SuiviPlus.dto.ProjectUpdateDTO;
import com.TgvTeam.SuiviPlus.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // ================================
    // GET mes projets
    // CORRECTION : Project → ProjectResponseDTO (évite boucle JSON infinie)
    // ================================
    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponseDTO>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    // ================================
    // POST créer un projet
    // CORRECTION : retourne 201 Created + ProjectResponseDTO
    // ================================
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @RequestBody ProjectCreateDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(dto));
    }

    // ================================
    // GET projet par ID
    // CORRECTION : Project → ProjectResponseDTO
    // ================================
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // ================================
    // PUT modifier un projet
    // CORRECTION : Project → ProjectResponseDTO
    // ================================
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable UUID id,
            @RequestBody ProjectUpdateDTO dto
    ) {
        return ResponseEntity.ok(projectService.updateProject(id, dto));
    }

    // ================================
    // DELETE supprimer un projet
    // CORRECTION : void → ResponseEntity<Void> avec 204 No Content
    // ================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // ================================
    // POST ajouter un membre
    // CORRECTION : userId dans le body JSON plutôt qu'en PathVariable
    // → plus cohérent avec le reste de l'API (cf. API.md)
    // ================================
    @PostMapping("/{projectId}/members")
    public ResponseEntity<Map<String, String>> addMember(
            @PathVariable UUID projectId,
            @RequestBody Map<String, UUID> body
    ) {
        UUID userId = body.get("userId");
        projectService.addMember(projectId, userId);
        return ResponseEntity.ok(Map.of("message", "Membre ajouté avec succès"));
    }

    // ================================
    // DELETE retirer un membre
    // CORRECTION : retourne 204 No Content
    // ================================
    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID projectId,
            @PathVariable UUID userId
    ) {
        projectService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}