package com.SuiviPlus.Tgv.entity;

import com.SuiviPlus.Tgv.entity.composite.UserTacheId;
import com.SuiviPlus.Tgv.enums.TypeAssignation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserTache {

//    🔑 utilisateur_id : UUID PK,FK → UTILISATEUR
//    🔑 tache_id : UUID PK,FK → TACHE
//    type_assignation : VARCHAR(50)
//    CHECK IN (responsable,
//              reviseur, observateur)
//    assigne_le : TIMESTAMP
//    assigne_par : UUID FK → UTILISATEUR

    @EmbeddedId
    private UserTacheId id;

    @Enumerated(EnumType.STRING)
    private TypeAssignation typeAssignation ;

    private LocalDateTime assigneLe;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("tacheId")
    @JoinColumn(name = "tache_id")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "assigne_par")
    private User assignePar;
}
