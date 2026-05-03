package com.SuiviPlus.Tgv.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Commentaire {

//    🔑 id : UUID PK
//    tache_id : UUID FK → TACHE
//    auteur_id : UUID FK → UTILISATEUR
//    parent_commentaire_id : UUID FK → COMMENTAIRE
//    contenu : TEXT
//    est_modifie : BOOLEAN DEFAULT FALSE
//    created_at : TIMESTAMP
//    updated_at : TIMESTAMP
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User auteur;

    @OneToMany (mappedBy = "parentCommentaire")
    private List<Commentaire> sousCommentaire;

    @ManyToOne
    @JoinColumn(name = "parent_commentaire_id")
    private  Commentaire ParentCommentaire;

    private String contenu;
    private boolean estModidie;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
