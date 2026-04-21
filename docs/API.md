# 📘 Documentation API — Plateforme de Collaboration Interne

> **Base URL :** `http://localhost:8080/api`
> **Format :** JSON (Content-Type: application/json)
> **Authentification :** Token JWT dans le header → `Authorization: Bearer <token>`

---

## Légende des accès

| Symbole | Signification |
|---|---|
| 🌐 Public | Aucun token requis |
| 🔒 Connecté | Token JWT valide requis |
| 👑 Admin | Token JWT + rôle ADMIN |
| ✍️ Propriétaire | Token JWT + être le créateur de la ressource |

---

## 🔐 Module 1 — Authentification `/api/auth`

---

### POST `/api/auth/register` — Inscription
**Accès :** 🌐 Public

**Body (requête) :**
```json
{
  "firstName": "Jean",
  "lastName":  "Dupont",
  "email":     "jean.dupont@company.com",
  "password":  "motdepasse123"
}
```

**Réponse succès `201 Created` :**
```json
{
  "message": "Compte créé avec succès"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Champ manquant ou vide |
| `400` | Format email invalide |
| `400` | Mot de passe trop court (< 8 caractères) |
| `409` | Email déjà utilisé par un autre compte |

---

### POST `/api/auth/login` — Connexion
**Accès :** 🌐 Public

**Body (requête) :**
```json
{
  "email":    "jean.dupont@company.com",
  "password": "motdepasse123"
}
```

**Réponse succès `200 OK` :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
  "user": {
    "id":        1,
    "firstName": "Jean",
    "lastName":  "Dupont",
    "email":     "jean.dupont@company.com",
    "role":      "MEMBER"
  }
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Champ email ou mot de passe manquant |
| `401` | Email introuvable ou mot de passe incorrect |

---

## 👤 Module 2 — Utilisateurs `/api/users`

---

### GET `/api/users/me` — Voir son propre profil
**Accès :** 🔒 Connecté

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
{
  "id":        1,
  "firstName": "Jean",
  "lastName":  "Dupont",
  "email":     "jean.dupont@company.com",
  "role":      "MEMBER",
  "createdAt": "2025-04-01T08:00:00"
}
```

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |

---

### PUT `/api/users/me` — Modifier son propre profil
**Accès :** 🔒 Connecté

**Body (requête) :**
```json
{
  "firstName": "Jean",
  "lastName":  "Martin",
  "password":  "nouveaumotdepasse456"
}
```
> Tous les champs sont optionnels. On n'envoie que ce qu'on veut modifier.

**Réponse succès `200 OK` :**
```json
{
  "id":        1,
  "firstName": "Jean",
  "lastName":  "Martin",
  "email":     "jean.dupont@company.com",
  "role":      "MEMBER"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Nouveau mot de passe trop court |
| `401` | Token absent ou expiré |

---

### GET `/api/users` — Lister tous les membres
**Accès :** 👑 Admin

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
[
  {
    "id":        1,
    "firstName": "Jean",
    "lastName":  "Dupont",
    "email":     "jean.dupont@company.com",
    "role":      "MEMBER"
  },
  {
    "id":        2,
    "firstName": "Alice",
    "lastName":  "Bernard",
    "email":     "alice.bernard@company.com",
    "role":      "ADMIN"
  }
]
```

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Utilisateur connecté mais pas ADMIN |

---

### PUT `/api/users/{id}/role` — Changer le rôle d'un membre
**Accès :** 👑 Admin

**Body (requête) :**
```json
{
  "role": "ADMIN"
}
```
> Valeurs acceptées pour `role` : `"MEMBER"` ou `"ADMIN"`

**Réponse succès `200 OK` :**
```json
{
  "id":    3,
  "email": "paul.durand@company.com",
  "role":  "ADMIN"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Valeur de rôle invalide (ni MEMBER ni ADMIN) |
| `401` | Token absent ou expiré |
| `403` | Utilisateur connecté mais pas ADMIN |
| `404` | Utilisateur avec cet id introuvable |

---

## 📁 Module 3 — Projets `/api/projects`

---

### GET `/api/projects` — Lister ses projets
**Accès :** 🔒 Connecté

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
[
  {
    "id":          12,
    "name":        "Refonte site web",
    "description": "Moderniser le site corporate",
    "status":      "IN_PROGRESS",
    "deadline":    "2025-06-30",
    "createdBy": {
      "id":        1,
      "firstName": "Jean"
    },
    "memberCount": 4,
    "taskCount":   10
  }
]
```
> Retourne uniquement les projets dont l'utilisateur est créateur ou membre.

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |

---

### POST `/api/projects` — Créer un projet
**Accès :** 🔒 Connecté

**Body (requête) :**
```json
{
  "name":        "Refonte site web",
  "description": "Moderniser le site corporate",
  "deadline":    "2025-06-30"
}
```

**Réponse succès `201 Created` :**
```json
{
  "id":          12,
  "name":        "Refonte site web",
  "description": "Moderniser le site corporate",
  "status":      "IN_PROGRESS",
  "deadline":    "2025-06-30",
  "createdBy": {
    "id":        1,
    "firstName": "Jean"
  },
  "createdAt":   "2025-04-17T10:30:00"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Champ `name` manquant ou vide |
| `400` | Date deadline dans le passé |
| `401` | Token absent ou expiré |

---

### GET `/api/projects/{id}` — Voir un projet en détail
**Accès :** 🔒 Membre du projet

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
{
  "id":          12,
  "name":        "Refonte site web",
  "description": "Moderniser le site corporate",
  "status":      "IN_PROGRESS",
  "deadline":    "2025-06-30",
  "createdBy": {
    "id":        1,
    "firstName": "Jean"
  },
  "members": [
    { "id": 1, "firstName": "Jean",  "role": "MEMBER" },
    { "id": 2, "firstName": "Alice", "role": "MEMBER" }
  ],
  "createdAt":   "2025-04-17T10:30:00"
}
```

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre de ce projet |
| `404` | Projet avec cet id introuvable |

---

### PUT `/api/projects/{id}` — Modifier un projet
**Accès :** ✍️ Propriétaire ou 👑 Admin

**Body (requête) :**
```json
{
  "name":        "Refonte site web v2",
  "description": "Description mise à jour",
  "deadline":    "2025-08-01",
  "status":      "IN_PROGRESS"
}
```
> Valeurs acceptées pour `status` : `"IN_PROGRESS"`, `"ON_HOLD"`, `"COMPLETED"`, `"CANCELLED"`

**Réponse succès `200 OK` :**
```json
{
  "id":          12,
  "name":        "Refonte site web v2",
  "description": "Description mise à jour",
  "status":      "IN_PROGRESS",
  "deadline":    "2025-08-01"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Valeur de statut invalide |
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet avec cet id introuvable |

---

### DELETE `/api/projects/{id}` — Supprimer un projet
**Accès :** ✍️ Propriétaire ou 👑 Admin

**Body :** Aucun

**Réponse succès `204 No Content` :** *(corps vide)*

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet avec cet id introuvable |

---

### POST `/api/projects/{id}/members` — Ajouter un membre au projet
**Accès :** ✍️ Propriétaire ou 👑 Admin

**Body (requête) :**
```json
{
  "userId": 5
}
```

**Réponse succès `200 OK` :**
```json
{
  "message": "Membre ajouté avec succès",
  "user": {
    "id":        5,
    "firstName": "Paul",
    "email":     "paul.durand@company.com"
  }
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Utilisateur déjà membre du projet |
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet ou utilisateur introuvable |

---

### DELETE `/api/projects/{id}/members/{userId}` — Retirer un membre
**Accès :** ✍️ Propriétaire ou 👑 Admin

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
{
  "message": "Membre retiré avec succès"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Impossible de retirer le créateur du projet |
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet ou utilisateur introuvable |
| `404` | Cet utilisateur n'est pas membre de ce projet |

---

## ✅ Module 4 — Tâches `/api/projects/{projectId}/tasks`

---

### GET `/api/projects/{projectId}/tasks` — Lister les tâches d'un projet
**Accès :** 🔒 Membre du projet

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
[
  {
    "id":          7,
    "title":       "Créer la maquette Figma",
    "description": "Maquette pour les 5 pages principales",
    "status":      "IN_PROGRESS",
    "priority":    "HIGH",
    "deadline":    "2025-05-10",
    "assignedTo": {
      "id":        2,
      "firstName": "Alice"
    }
  }
]
```

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre de ce projet |
| `404` | Projet introuvable |

---

### POST `/api/projects/{projectId}/tasks` — Créer une tâche
**Accès :** 🔒 Membre du projet

**Body (requête) :**
```json
{
  "title":       "Créer la maquette Figma",
  "description": "Maquette pour les 5 pages principales",
  "priority":    "HIGH",
  "deadline":    "2025-05-10",
  "assignedToId": 2
}
```
> Valeurs acceptées pour `priority` : `"LOW"`, `"MEDIUM"`, `"HIGH"`, `"CRITICAL"`
> `assignedToId` est optionnel (la tâche peut être non assignée à la création)

**Réponse succès `201 Created` :**
```json
{
  "id":          7,
  "title":       "Créer la maquette Figma",
  "status":      "TODO",
  "priority":    "HIGH",
  "deadline":    "2025-05-10",
  "assignedTo": {
    "id":        2,
    "firstName": "Alice"
  },
  "createdAt":   "2025-04-17T11:00:00"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Champ `title` manquant |
| `400` | Valeur de priorité invalide |
| `400` | `assignedToId` n'est pas membre du projet |
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre de ce projet |
| `404` | Projet introuvable |

---

### GET `/api/projects/{projectId}/tasks/{taskId}` — Voir une tâche en détail
**Accès :** 🔒 Membre du projet

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
{
  "id":          7,
  "title":       "Créer la maquette Figma",
  "description": "Maquette pour les 5 pages principales",
  "status":      "IN_PROGRESS",
  "priority":    "HIGH",
  "deadline":    "2025-05-10",
  "assignedTo": {
    "id":        2,
    "firstName": "Alice"
  },
  "project": {
    "id":   12,
    "name": "Refonte site web"
  },
  "commentCount": 3,
  "createdAt":    "2025-04-17T11:00:00"
}
```

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre de ce projet |
| `404` | Projet ou tâche introuvable |

---

### PUT `/api/projects/{projectId}/tasks/{taskId}` — Modifier une tâche
**Accès :** ✍️ Propriétaire du projet ou 👑 Admin

**Body (requête) :**
```json
{
  "title":        "Créer la maquette Figma (v2)",
  "description":  "Description révisée",
  "priority":     "CRITICAL",
  "deadline":     "2025-05-05",
  "assignedToId": 3
}
```

**Réponse succès `200 OK` :**
```json
{
  "id":       7,
  "title":    "Créer la maquette Figma (v2)",
  "priority": "CRITICAL",
  "deadline": "2025-05-05"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Valeur de priorité invalide |
| `400` | `assignedToId` n'est pas membre du projet |
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet ou tâche introuvable |

---

### DELETE `/api/projects/{projectId}/tasks/{taskId}` — Supprimer une tâche
**Accès :** ✍️ Propriétaire du projet ou 👑 Admin

**Body :** Aucun

**Réponse succès `204 No Content` :** *(corps vide)*

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet ou tâche introuvable |

---

### PATCH `/api/projects/{projectId}/tasks/{taskId}/status` — Changer le statut d'une tâche
**Accès :** 🔒 Membre du projet (y compris la personne assignée)

**Body (requête) :**
```json
{
  "status": "IN_PROGRESS"
}
```
> **Cycle de statut :** `TODO` → `IN_PROGRESS` → `IN_REVIEW` → `DONE`
> Valeurs acceptées : `"TODO"`, `"IN_PROGRESS"`, `"IN_REVIEW"`, `"DONE"`

**Réponse succès `200 OK` :**
```json
{
  "id":     7,
  "status": "IN_PROGRESS"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Valeur de statut invalide |
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre de ce projet |
| `404` | Projet ou tâche introuvable |

---

### POST `/api/projects/{projectId}/tasks/{taskId}/assign` — Assigner une tâche à un membre
**Accès :** ✍️ Propriétaire du projet ou 👑 Admin

**Body (requête) :**
```json
{
  "userId": 3
}
```

**Réponse succès `200 OK` :**
```json
{
  "id":    7,
  "title": "Créer la maquette Figma",
  "assignedTo": {
    "id":        3,
    "firstName": "Paul"
  }
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | `userId` n'est pas membre du projet |
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni propriétaire ni admin |
| `404` | Projet, tâche ou utilisateur introuvable |

---

## 💬 Module 5 — Commentaires `/api/tasks/{taskId}/comments`

---

### GET `/api/tasks/{taskId}/comments` — Lire les commentaires d'une tâche
**Accès :** 🔒 Membre du projet parent

**Body :** Aucun

**Réponse succès `200 OK` :**
```json
[
  {
    "id":      1,
    "content": "J'ai commencé la maquette, revue demain.",
    "author": {
      "id":        2,
      "firstName": "Alice"
    },
    "createdAt": "2025-04-17T14:00:00"
  },
  {
    "id":      2,
    "content": "OK, je ferai la revue après 14h.",
    "author": {
      "id":        1,
      "firstName": "Jean"
    },
    "createdAt": "2025-04-17T14:15:00"
  }
]
```

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre du projet parent |
| `404` | Tâche introuvable |

---

### POST `/api/tasks/{taskId}/comments` — Ajouter un commentaire
**Accès :** 🔒 Membre du projet parent

**Body (requête) :**
```json
{
  "content": "J'ai commencé la maquette, revue demain."
}
```

**Réponse succès `201 Created` :**
```json
{
  "id":      3,
  "content": "J'ai commencé la maquette, revue demain.",
  "author": {
    "id":        2,
    "firstName": "Alice"
  },
  "createdAt": "2025-04-17T15:00:00"
}
```

| Code | Cas d'erreur |
|---|---|
| `400` | Champ `content` vide |
| `401` | Token absent ou expiré |
| `403` | Connecté mais pas membre du projet parent |
| `404` | Tâche introuvable |

---

### DELETE `/api/tasks/{taskId}/comments/{commentId}` — Supprimer un commentaire
**Accès :** ✍️ Auteur du commentaire ou 👑 Admin

**Body :** Aucun

**Réponse succès `204 No Content` :** *(corps vide)*

| Code | Cas d'erreur |
|---|---|
| `401` | Token absent ou expiré |
| `403` | Connecté mais ni auteur ni admin |
| `404` | Tâche ou commentaire introuvable |

---

## 📊 Récapitulatif complet des endpoints

| # | Méthode | URL | Action | Accès |
|---|---|---|---|---|
| 1 | `POST` | `/api/auth/register` | Inscription | 🌐 Public |
| 2 | `POST` | `/api/auth/login` | Connexion | 🌐 Public |
| 3 | `GET` | `/api/users/me` | Voir son profil | 🔒 Connecté |
| 4 | `PUT` | `/api/users/me` | Modifier son profil | 🔒 Connecté |
| 5 | `GET` | `/api/users` | Lister tous les membres | 👑 Admin |
| 6 | `PUT` | `/api/users/{id}/role` | Changer un rôle | 👑 Admin |
| 7 | `GET` | `/api/projects` | Lister ses projets | 🔒 Connecté |
| 8 | `POST` | `/api/projects` | Créer un projet | 🔒 Connecté |
| 9 | `GET` | `/api/projects/{id}` | Voir un projet | 🔒 Membre |
| 10 | `PUT` | `/api/projects/{id}` | Modifier un projet | ✍️ / 👑 |
| 11 | `DELETE` | `/api/projects/{id}` | Supprimer un projet | ✍️ / 👑 |
| 12 | `POST` | `/api/projects/{id}/members` | Ajouter un membre | ✍️ / 👑 |
| 13 | `DELETE` | `/api/projects/{id}/members/{userId}` | Retirer un membre | ✍️ / 👑 |
| 14 | `GET` | `/api/projects/{projectId}/tasks` | Lister les tâches | 🔒 Membre |
| 15 | `POST` | `/api/projects/{projectId}/tasks` | Créer une tâche | 🔒 Membre |
| 16 | `GET` | `/api/projects/{projectId}/tasks/{taskId}` | Voir une tâche | 🔒 Membre |
| 17 | `PUT` | `/api/projects/{projectId}/tasks/{taskId}` | Modifier une tâche | ✍️ / 👑 |
| 18 | `DELETE` | `/api/projects/{projectId}/tasks/{taskId}` | Supprimer une tâche | ✍️ / 👑 |
| 19 | `PATCH` | `/api/projects/{projectId}/tasks/{taskId}/status` | Changer le statut | 🔒 Membre |
| 20 | `POST` | `/api/projects/{projectId}/tasks/{taskId}/assign` | Assigner une tâche | ✍️ / 👑 |
| 21 | `GET` | `/api/tasks/{taskId}/comments` | Lire les commentaires | 🔒 Membre |
| 22 | `POST` | `/api/tasks/{taskId}/comments` | Ajouter un commentaire | 🔒 Membre |
| 23 | `DELETE` | `/api/tasks/{taskId}/comments/{commentId}` | Supprimer un commentaire | ✍️ / 👑 |

---

*Dernière mise à jour : Avril 2025*
