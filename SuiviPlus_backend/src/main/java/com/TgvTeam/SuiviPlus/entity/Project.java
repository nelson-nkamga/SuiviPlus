package com.TgvTeam.SuiviPlus.entity;


import com.TgvTeam.SuiviPlus.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "projet")
public class Project {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus statut;

    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;
    private LocalDate dateFinReelle;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // RELATIONS

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private User createur;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @OneToMany(mappedBy = "project")
    private List<UserProject> membres;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.statut = ProjectStatus.ACTIF;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}