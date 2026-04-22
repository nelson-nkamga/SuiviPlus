package com.TgvTeam.SuiviPlus.entity;

import com.TgvTeam.SuiviPlus.enums.Priority;
import com.TgvTeam.SuiviPlus.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "tache")
public class Task {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 300)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private Priority priorite;

    private Integer estimationHeures;

    private LocalDate dateEcheance;

    private LocalDateTime dateDebutReelle;
    private LocalDateTime dateFinReelle;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =======================
    // RELATIONS
    // =======================

    /**
     * Une tâche appartient à un projet
     */
    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Project project;

    /**
     * Créateur de la tâche
     */
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private User createur;

    /**
     * Assignations enrichies (remplace ManyToMany)
     * User ↔ Task avec rôle, date, etc.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTask> assignations;

    /**
     * Commentaires liés à la tâche
     */
    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    /**
     * Tâche parent (sous-tâches)
     */
    @ManyToOne
    @JoinColumn(name = "parent_tache_id")
    private Task parentTask;

    /**
     * Sous-tâches
     */
    @OneToMany(mappedBy = "parentTask")
    private List<Task> subTasks;

    // =======================
    // AUDIT
    // =======================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = TaskStatus.A_FAIRE;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}