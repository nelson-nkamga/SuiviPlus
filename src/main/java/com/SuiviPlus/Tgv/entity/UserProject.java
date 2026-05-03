package com.SuiviPlus.Tgv.entity;

import com.SuiviPlus.Tgv.entity.composite.UserProjectId;
import com.SuiviPlus.Tgv.enums.RoleProject;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserProject {

//    🔑 user_id : UUID PK,FK → UTILISATEUR
//    🔑 projet_id : UUID PK,FK → PROJET
//    role_projet : ENUM(chef_projet, contributeur, observateur)
//    rejoint_le : TIMESTAMP

    @EmbeddedId
    private UserProjectId id;

    @Enumerated(EnumType.STRING) // signifi tu stockes le role du project sous forme de string
    private RoleProject roleProject ;
    private LocalDateTime rejointLe;

    @ManyToOne
    @MapsId("userId") // indique que la relation remplit une partie de la cle composite
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project projectId;


}
