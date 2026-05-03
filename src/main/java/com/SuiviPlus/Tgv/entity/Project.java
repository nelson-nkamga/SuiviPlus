package com.SuiviPlus.Tgv.entity;

import com.SuiviPlus.Tgv.enums.StatutProject;
import com.SuiviPlus.Tgv.entity.User;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Project {

//    🔑 id : UUID PK
//    nom : VARCHAR(200)
//    description : TEXT
//    statut : ENUM(actif, archive, termine)
//    createur_id : UUID FK → UTILISATEUR
//    date_debut : DATE
//    date_fin_prevue : DATE
//    date_fin_reelle : DATE
//    created_at : TIMESTAMP
//    updated_at : TIMESTAMP

    @Id
    @GeneratedValue
    private UUID id;
    private String nom;
    private String description;

    @Enumerated(EnumType.STRING)
    private StatutProject statut;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFinPrevue;
    private LocalDateTime dateFinReelle;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "createur_id" , nullable = false) // indique que la colonne user  dans la table  actuel(project) sert de cle etrangere
    private User createurId;

    @OneToMany(mappedBy = "projectId")
    private List<UserProject> utilisateur;

}
