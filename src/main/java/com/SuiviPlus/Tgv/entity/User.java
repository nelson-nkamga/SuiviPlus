package com.SuiviPlus.Tgv.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class User {
//     🔑 id : UUID PK
//    nom : VARCHAR(100)
//    prenom : VARCHAR(100)
//    email : VARCHAR(255) UNIQUE
//    mot_de_passe_hash : VARCHAR(255)
//    avatar_url : VARCHAR(500)
//    est_actif : BOOLEAN DEFAULT TRUE
//    date_inscription : TIMESTAMP
//    created_at : TIMESTAMP
//    updated_at : TIMESTAMP

    @Id
    @GeneratedValue
    private UUID id;
    private String nom ;
    private String prenom ;

    @Column(unique = true , nullable = false)
    private String email ;
    private String motDePasseHash ;
    private String avatarUrl ;
    private Boolean estActif ;
    private LocalDateTime dateInscription ;
    private LocalDateTime createAt;
    private LocalDateTime updateAt ;

    @OneToMany(mappedBy = "userId")
    private List<UserProject> projects;




}
