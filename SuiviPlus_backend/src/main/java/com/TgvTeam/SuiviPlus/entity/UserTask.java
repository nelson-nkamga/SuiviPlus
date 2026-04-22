package com.TgvTeam.SuiviPlus.entity;

import com.TgvTeam.SuiviPlus.entity.composite.UserTaskId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_task")
public class UserTask {

    @EmbeddedId
    private UserTaskId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "utilisateur_id")
    private User assignee;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "tache_id")
    private Task task;

    private String typeAssignation; // responsable, reviseur, observateur

    private LocalDateTime assigneLe;

    @ManyToOne
    @JoinColumn(name = "assigne_par")
    private User assignePar;
}
