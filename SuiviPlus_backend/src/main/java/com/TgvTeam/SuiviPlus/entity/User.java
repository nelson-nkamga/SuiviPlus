package com.TgvTeam.SuiviPlus.entity;

import com.TgvTeam.SuiviPlus.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // ← import manquant

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "utilisateur")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 100, nullable = false)
    private String nom;

    @Column(length = 100, nullable = false)
    private String prenom;

    @Column(unique = true, length = 255, nullable = false)
    private String email;

    @Column(name = "mot_de_passe_hash", nullable = false)
    private String password; // ← le champ s'appelle password dans ton entité

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    private Boolean estActif = true;

    private LocalDateTime dateInscription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // =======================
    // RELATIONS
    // =======================

    @OneToMany(mappedBy = "createur")
    private List<Project> projetsCrees;

    @OneToMany(mappedBy = "user")
    private List<UserProject> projets;

    @OneToMany(mappedBy = "assignee")
    private List<UserTask> tasks;

    @OneToMany(mappedBy = "auteur")
    private List<Comment> commentaires;

    // =======================
    // AUDIT
    // =======================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.dateInscription = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.estActif = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =======================
    // UserDetails — Spring Security
    // =======================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.password; // ✅ corrigé : le champ s'appelle password, pas motDePasseHash
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(this.estActif); // ✅ null-safe : Boolean peut être null
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.estActif); // ✅ null-safe
    }
}
