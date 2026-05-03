package com.SuiviPlus.Tgv.entity;

import com.SuiviPlus.Tgv.enums.PrioriteTache;
import com.SuiviPlus.Tgv.enums.StatutTache;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Tache {
//    🔑 id : UUID PK
//    projet_id : UUID FK → PROJET
//    parent_tache_id : UUID FK → TACHE (self)
//    createur_id : UUID FK → UTILISATEUR
//    titre : VARCHAR(300)
//    description : TEXT
//    statut : ENUM(a_faire, en_cours,
//                  terminee, bloquee)
//    priorite : ENUM(basse, moyenne,
//                    haute, critique)
//    estimation_heures : INT
//    date_echeance : DATE
//    date_debut_reelle : TIMESTAMP
//    date_fin_reelle : TIMESTAMP
//    created_at : TIMESTAMP
//    updated_at : TIMESTAMP

    @Id
    @GeneratedValue
    private UUID id ;

    private String titre;
    private String description;
    private int estimationHeures;
    private LocalDateTime dateEcheance;
    private LocalDateTime dateDebutReelle;
    private LocalDateTime dateFinReelle;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private User createur;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private StatutTache statut;

    @Enumerated(EnumType.STRING)
    private PrioriteTache priorite;

    @ManyToOne
    @JoinColumn(name = "parent_tache_id")
    private Tache parentTache;

    @OneToMany(mappedBy = "parentTache")
    private List<Tache> sousTaches;

    @OneToMany(mappedBy = "tache")
    private List<Commentaire> commentaire;
}
