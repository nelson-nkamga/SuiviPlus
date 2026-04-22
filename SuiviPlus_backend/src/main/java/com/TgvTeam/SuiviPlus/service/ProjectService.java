package com.TgvTeam.SuiviPlus.service;

import com.TgvTeam.SuiviPlus.dto.ProjectCreateDTO;
import com.TgvTeam.SuiviPlus.dto.ProjectResponseDTO;
import com.TgvTeam.SuiviPlus.dto.ProjectUpdateDTO;
import com.TgvTeam.SuiviPlus.entity.Project;
import com.TgvTeam.SuiviPlus.entity.User;
import com.TgvTeam.SuiviPlus.entity.UserProject;
import com.TgvTeam.SuiviPlus.entity.composite.UserProjectId;
import com.TgvTeam.SuiviPlus.enums.ProjectStatus;
import com.TgvTeam.SuiviPlus.enums.Role;
import com.TgvTeam.SuiviPlus.exception.BadRequestException;
import com.TgvTeam.SuiviPlus.exception.ForbiddenException;
import com.TgvTeam.SuiviPlus.exception.ResourceNotFoundException;
import com.TgvTeam.SuiviPlus.mapper.ProjectMapper;
import com.TgvTeam.SuiviPlus.repository.ProjectRepository;
import com.TgvTeam.SuiviPlus.repository.UserProjectRepository;
import com.TgvTeam.SuiviPlus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    // ================================
    // GET mes projets
    // ================================
    public List<ProjectResponseDTO> getMyProjects() {
        User user = getCurrentUser();

        List<Project> created = projectRepository.findByCreateurId(user.getId());
        List<Project> member  = projectRepository.findByMembres_User_Id(user.getId());

        Set<Project> all = new HashSet<>();
        all.addAll(created);
        all.addAll(member);

        return all.stream()
                .map(ProjectMapper::toDTO)
                .toList();
    }

    // ================================
    // CREATE projet
    // ================================
    public ProjectResponseDTO createProject(ProjectCreateDTO dto) {

        User user = getCurrentUser();

        Project project = new Project();
        project.setNom(dto.getNom());
        project.setDescription(dto.getDescription());
        project.setCreateur(user);
        project.setStatut(ProjectStatus.ACTIF);

        Project saved = projectRepository.save(project);

        // ✅ CORRECTION : initialiser UserProjectId avant de sauvegarder
        UserProjectId upId = new UserProjectId();
        upId.setProjectId(saved.getId());
        upId.setUserId(user.getId());

        UserProject up = new UserProject();
        up.setId(upId);
        up.setProject(saved);
        up.setUser(user);

        userProjectRepository.save(up);

        return ProjectMapper.toDTO(saved);
    }

    // ================================
    // GET projet par ID
    // ================================
    public ProjectResponseDTO getProjectById(UUID id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        User user = getCurrentUser();

        boolean isMember =
                userProjectRepository.existsByProjectIdAndUserId(id, user.getId())
                        || project.getCreateur().getId().equals(user.getId());

        if (!isMember) {
            throw new ForbiddenException("Accès interdit");
        }

        return ProjectMapper.toDTO(project);
    }

    // ================================
    // UPDATE projet
    // ================================
    public ProjectResponseDTO updateProject(UUID id, ProjectUpdateDTO dto) {

        Project project = getProjectEntityById(id);
        User user = getCurrentUser();

        if (!project.getCreateur().getId().equals(user.getId())
                && !user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        if (project.getStatut() == ProjectStatus.TERMINE) {
            throw new BadRequestException("Projet terminé non modifiable");
        }

        if (dto.getNom() != null)         project.setNom(dto.getNom());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getStatut() != null)      project.setStatut(dto.getStatut());

        return ProjectMapper.toDTO(projectRepository.save(project));
    }

    // ================================
    // DELETE projet
    // ================================
    public void deleteProject(UUID id) {

        Project project = getProjectEntityById(id);
        User user = getCurrentUser();

        if (!project.getCreateur().getId().equals(user.getId())
                && !user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        projectRepository.delete(project);
    }

    // ================================
    // ADD membre
    // ================================
    public void addMember(UUID projectId, UUID userId) {

        Project project = getProjectEntityById(projectId);
        User current = getCurrentUser();

        if (!project.getCreateur().getId().equals(current.getId())
                && !current.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (userProjectRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new BadRequestException("Déjà membre du projet");
        }

        // ✅ CORRECTION : initialiser UserProjectId
        UserProjectId upId = new UserProjectId();
        upId.setProjectId(projectId);
        upId.setUserId(userId);

        UserProject up = new UserProject();
        up.setId(upId);
        up.setProject(project);
        up.setUser(userToAdd);

        userProjectRepository.save(up);
    }

    // ================================
    // REMOVE membre
    // ================================
    public void removeMember(UUID projectId, UUID userId) {

        Project project = getProjectEntityById(projectId);
        User current = getCurrentUser();

        if (!project.getCreateur().getId().equals(current.getId())
                && !current.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        if (project.getCreateur().getId().equals(userId)) {
            throw new BadRequestException("Impossible de retirer le créateur");
        }

        userProjectRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    // ================================
    // HELPERS INTERNES
    // ================================

    /**
     * Charge l'entité Project brute — usage interne uniquement.
     * Le Controller n'appelle jamais cette méthode directement.
     */
    private Project getProjectEntityById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));
    }

    /**
     * Récupère l'utilisateur connecté depuis le SecurityContext.
     * Fonctionne car JwtFilter place l'entité User directement dans le principal.
     */
    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}