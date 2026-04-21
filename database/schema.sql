-- ============================================================
--  SCRIPT DE CRÉATION — BASE DE DONNÉES GESTION DE PROJETS
--  SGBD : PostgreSQL 14+
-- ============================================================


-- ------------------------------------------------------------
-- 0. EXTENSION UUID
-- ------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- ============================================================
-- 1. TYPES ÉNUMÉRÉS
--    ⚠️  À déclarer AVANT les tables qui les utilisent.
-- ============================================================

CREATE TYPE statut_projet AS ENUM (
    'actif',
    'archive',
    'termine'
);

CREATE TYPE role_projet AS ENUM (
    'chef_projet',
    'contributeur',
    'observateur'
);

CREATE TYPE statut_tache AS ENUM (
    'a_faire',
    'en_cours',
    'terminee',
    'bloquee'
);

CREATE TYPE priorite_tache AS ENUM (
    'basse',
    'moyenne',
    'haute',
    'critique'
);


-- ============================================================
-- 2. TABLE : utilisateur
-- ============================================================

CREATE TABLE utilisateur (
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom               VARCHAR(100) NOT NULL,
    prenom            VARCHAR(100) NOT NULL,
    email             VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe_hash VARCHAR(255) NOT NULL,
    avatar_url        VARCHAR(500),
    est_actif         BOOLEAN      NOT NULL DEFAULT TRUE,
    date_inscription  TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Recherche par email (connexion, vérification de doublon)
CREATE INDEX idx_utilisateur_email ON utilisateur (email);


-- ============================================================
-- 3. TABLE : projet
-- ============================================================

CREATE TABLE projet (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    nom             VARCHAR(200)  NOT NULL,
    description     TEXT,
    statut          statut_projet NOT NULL DEFAULT 'actif',
    createur_id     UUID          NOT NULL
                        REFERENCES utilisateur(id)
                        ON DELETE RESTRICT,
    -- RESTRICT : impossible de supprimer un utilisateur
    --            tant qu'il possède des projets.
    date_debut      DATE,
    date_fin_prevue DATE,
    date_fin_reelle DATE,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_projet_dates CHECK (
        date_fin_prevue IS NULL
        OR date_debut IS NULL
        OR date_fin_prevue >= date_debut
    )
);

CREATE INDEX idx_projet_createur ON projet (createur_id);
CREATE INDEX idx_projet_statut   ON projet (statut);


-- ============================================================
-- 4. TABLE : utilisateur_projet  (table de jonction N-N)
-- ============================================================

CREATE TABLE utilisateur_projet (
    utilisateur_id UUID        NOT NULL
                       REFERENCES utilisateur(id)
                       ON DELETE CASCADE,
    -- CASCADE : si l'utilisateur est supprimé,
    --           ses appartenances aux projets aussi.
    projet_id      UUID        NOT NULL
                       REFERENCES projet(id)
                       ON DELETE CASCADE,
    -- CASCADE : si le projet est supprimé,
    --           toutes les adhésions disparaissent.
    role_projet    role_projet NOT NULL DEFAULT 'contributeur',
    rejoint_le     TIMESTAMP   NOT NULL DEFAULT NOW(),

    PRIMARY KEY (utilisateur_id, projet_id)
);

CREATE INDEX idx_up_projet ON utilisateur_projet (projet_id);


-- ============================================================
-- 5. TABLE : tache
-- ============================================================

CREATE TABLE tache (
    id                UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    projet_id         UUID           NOT NULL
                          REFERENCES projet(id)
                          ON DELETE CASCADE,
    -- CASCADE : supprimer un projet supprime toutes ses tâches.
    parent_tache_id   UUID
                          REFERENCES tache(id)
                          ON DELETE SET NULL,
    -- SET NULL : si la tâche parente est supprimée, les sous-tâches
    --            deviennent des tâches racines (elles ne sont pas perdues).
    createur_id       UUID
                          REFERENCES utilisateur(id)
                          ON DELETE SET NULL,
    -- SET NULL : la tâche est conservée même si son créateur est supprimé.
    titre             VARCHAR(300)   NOT NULL,
    description       TEXT,
    statut            statut_tache   NOT NULL DEFAULT 'a_faire',
    priorite          priorite_tache NOT NULL DEFAULT 'moyenne',
    estimation_heures INTEGER        CHECK (estimation_heures > 0),
    date_echeance     DATE,
    date_debut_reelle TIMESTAMP,
    date_fin_reelle   TIMESTAMP,
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_tache_dates CHECK (
        date_fin_reelle IS NULL
        OR date_debut_reelle IS NULL
        OR date_fin_reelle >= date_debut_reelle
    )
);

CREATE INDEX idx_tache_projet         ON tache (projet_id);
CREATE INDEX idx_tache_parent         ON tache (parent_tache_id);
CREATE INDEX idx_tache_createur       ON tache (createur_id);
CREATE INDEX idx_tache_statut         ON tache (statut);
CREATE INDEX idx_tache_date_echeance  ON tache (date_echeance);


-- ============================================================
-- 6. TABLE : utilisateur_tache  (table de jonction N-N)
-- ============================================================

CREATE TABLE utilisateur_tache (
    utilisateur_id   UUID        NOT NULL
                         REFERENCES utilisateur(id)
                         ON DELETE CASCADE,
    -- CASCADE : si l'utilisateur est supprimé,
    --           ses assignations disparaissent.
    tache_id         UUID        NOT NULL
                         REFERENCES tache(id)
                         ON DELETE CASCADE,
    -- CASCADE : si la tâche est supprimée,
    --           les assignations aussi.
    type_assignation VARCHAR(50) NOT NULL
                         CHECK (type_assignation IN ('responsable', 'reviseur', 'observateur')),
    assigne_le       TIMESTAMP   NOT NULL DEFAULT NOW(),
    assigne_par      UUID
                         REFERENCES utilisateur(id)
                         ON DELETE SET NULL,
    -- SET NULL : on garde la trace de l'assignation
    --            même si le manager qui l'a faite est supprimé.

    PRIMARY KEY (utilisateur_id, tache_id)
);

CREATE INDEX idx_ut_tache       ON utilisateur_tache (tache_id);
CREATE INDEX idx_ut_assigne_par ON utilisateur_tache (assigne_par);


-- ============================================================
-- 7. TABLE : commentaire
-- ============================================================

CREATE TABLE commentaire (
    id                    UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
    tache_id              UUID      NOT NULL
                              REFERENCES tache(id)
                              ON DELETE CASCADE,
    -- CASCADE : pas de commentaire sans tâche.
    auteur_id             UUID
                              REFERENCES utilisateur(id)
                              ON DELETE SET NULL,
    -- SET NULL : le commentaire reste visible avec "[utilisateur supprimé]".
    parent_commentaire_id UUID
                              REFERENCES commentaire(id)
                              ON DELETE CASCADE,
    -- CASCADE : supprimer un commentaire parent
    --           supprime aussi toutes ses réponses.
    contenu               TEXT      NOT NULL,
    est_modifie           BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_commentaire_tache  ON commentaire (tache_id);
CREATE INDEX idx_commentaire_auteur ON commentaire (auteur_id);
CREATE INDEX idx_commentaire_parent ON commentaire (parent_commentaire_id);
