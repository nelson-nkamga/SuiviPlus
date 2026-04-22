package com.TgvTeam.SuiviPlus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "commentaire")
public class Comment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    private Boolean estModifie = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // RELATIONS

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private User auteur;

    @ManyToOne
    @JoinColumn(name = "parent_commentaire_id")
    private Comment parentComment;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.estModifie = true;
    }
}
