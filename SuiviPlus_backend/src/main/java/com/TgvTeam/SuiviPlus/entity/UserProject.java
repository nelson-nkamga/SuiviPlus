package com.TgvTeam.SuiviPlus.entity;

import com.TgvTeam.SuiviPlus.entity.composite.UserProjectId;
import com.TgvTeam.SuiviPlus.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_projet")
public class UserProject {

    @EmbeddedId
    private UserProjectId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "utilisateur_id")
    private User user;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "projet_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private ProjectRole roleProjet;

    private LocalDateTime rejointLe;
}
