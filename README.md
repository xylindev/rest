# Documentation de l'API REST - Gestion des Déchets

**Date :** 16 mars 2026  
**Format supporté :** JSON, XML

### 1. Liste des points d'accès

| URI | Opération | MIME | Requête | Réponse |
| :--- | :--- | :--- | :--- | :--- |
| `/points` | GET | `<-application/json` `<-application/xml` | | Liste des points de collecte (21) |
| `/points/{id}` | GET | `<-application/json` `<-application/xml` | | Détail d'un point avec ses types de déchets acceptés (21b) |
| `/points/{id}` | PUT | `->application/json` | Point de collecte (21) | Point modifié ou 404 |
| `/points/{id}` | PATCH | `->application/json` | Champs partiels (21) | Point modifié ou 404 |
| `/points/{id}/status` | GET | `<-application/json` | | État de remplissage (22) |
| `/points/{id}/clear` | DELETE | | | 204 ou 404 |
| `/points/overloaded` | GET | `<-application/json` `<-application/xml` | | Liste des points saturés (22) |
| `/waste-types` | GET | `<-application/json` `<-application/xml` | | Liste des types de déchets (23) |
| `/waste-types/{id}` | GET | `<-application/json` `<-application/xml` | | Détail d'un type de déchet (23) |
| `/waste-types` | POST | `->application/json` | Type de déchet (24) | 201 ou 400 |
| `/waste-types/{id}` | PUT | `->application/json` | Type de déchet (24) | 200 ou 404 |
| `/waste-types/{id}` | DELETE | | | 204, 404 ou 409 |
| `/deposits` | GET | `<-application/json` `<-application/xml` | | Liste des dépôts enrichis (25) |
| `/deposits/{id}` | GET | `<-application/json` `<-application/xml` | | Détail d'un dépôt (26) |
| `/deposits` | POST | `->application/json` | Dépôt (26) | 201, 400 ou 403 |
| `/deposits/{id}` | PUT | `->application/json` | Dépôt (26) | 200 ou 404 |
| `/deposits/{id}` | PATCH | `->application/json` | Champs partiels (26) | 200 ou 404 |
| `/users/leaderboard` | GET | `<-application/json` `<-application/xml` | | Classement des utilisateurs (27) |
| `/users/{id}` | PUT | `->application/json` | Utilisateur (28) | 200 ou 404 |
| `/users/{id}` | PATCH | `->application/json` | Champs partiels (28) | 200 ou 404 |



### 2. Représentations des ressources

#### 2.1 Point de collecte (21)
Un point de collecte est défini par son identifiant, son adresse et sa capacité maximale.
```json
{
  "id": 1,
  "adresse": "123 Rue de la Récupération",
  "capaciteMax": 500
}
```

#### 2.1b Point de collecte avec déchets acceptés (21b)
Retourné par `GET /points/{id}`, inclut la liste imbriquée des types de déchets acceptés.
```json
{
  "id": 1,
  "adresse": "12 Rue de l'Innovation, Lille",
  "capaciteMax": 500,
  "acceptedWastes": [
    { "id": 1, "name": "Batteries", "pointsPerKilo": 15 },
    { "id": 3, "name": "Electroniques", "pointsPerKilo": 20 }
  ]
}
```

#### 2.2 État de remplissage d'un point (22)
```json
{
  "id": 1,
  "adresse": "12 Rue de l'Innovation, Lille",
  "remplissage": 73.5,
  "full": false
}
```
> `remplissage` est exprimé en pourcentage. `full` est `true` si la charge dépasse 100% de la capacité. Le calcul ne porte que sur les dépôts où `collecte = false`.

#### 2.3 Type de déchet (23)
Définit une catégorie de déchet et la valeur associée.
```json
{
  "id": 10,
  "name": "Plastique",
  "pointsPerKilo": 5
}
```

#### 2.4 Nouveau type de déchet (24)
Corps attendu pour `POST /waste-types` et `PUT /waste-types/{id}`.
```json
{
  "id": 6,
  "name": "Verre",
  "pointsPerKilo": 2
}
```

#### 2.5 Dépôt enrichi (25)
Retourné par `GET /deposits`. Inclut le nom du déchet et l'adresse du point à la place des IDs bruts.
```json
{
  "id": 1,
  "userId": 2,
  "wasteTypeName": "Batteries",
  "pointAdresse": "12 Rue de l'Innovation, Lille",
  "poids": 5.5,
  "dateDepot": "2026-03-16T10:00:00.000+00:00",
  "collecte": false
}
```

#### 2.6 Dépôt (26)
Utilisé pour `POST`, `PUT`, `PATCH /deposits` et `GET /deposits/{id}`.
```json
{
  "id": 1,
  "userId": 2,
  "pointId": 1,
  "wasteTypeId": 3,
  "poids": 5.5,
  "dateDepot": "2026-03-16T10:00:00.000+00:00",
  "collecte": false
}
```
> Pour `POST /deposits`, les champs `id`, `dateDepot` et `collecte` sont ignorés : l'id est auto-généré, la date est celle du serveur, et `collecte` démarre à `false`.

#### 2.7 Entrée du classement (27)
```json
{
  "id": 2,
  "login": "paul",
  "role": "USER",
  "score": 247.5
}
```
> `score = Σ(poids × pointsPerKilo)` sur tous les dépôts de l'utilisateur. Le champ `password` est présent dans la réponse actuelle — à masquer en Partie C.

#### 2.8 Utilisateur (28)
Corps attendu pour `PUT` et `PATCH /users/{id}`.
```json
{
  "login": "paul",
  "password": "nouveauMotDePasse",
  "role": "USER"
}
```



### 3. Opérations détaillées

#### Récupérer tous les points de collecte
**GET** `/points`

| Status | Description |
| :--- | :--- |
| 200 OK | Liste retournée (vide si aucun point) |

---

#### Récupérer le détail d'un point
**GET** `/points/{id}`

| Status | Description |
| :--- | :--- |
| 200 OK | Détail du point avec ses types de déchets acceptés |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Modifier un point de collecte (remplacement complet)
**PUT** `/points/{id}`

```json
{ "adresse": "Nouvelle adresse", "capaciteMax": 600 }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Point mis à jour |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Modifier partiellement un point de collecte
**PATCH** `/points/{id}`

```json
{ "adresse": "Nouvelle adresse" }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Champ(s) mis à jour |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Récupérer l'état d'un point
**GET** `/points/{id}/status`

**Réponse du serveur**
```json
{
  "id": 1,
  "adresse": "12 Rue de l'Innovation, Lille",
  "remplissage": 73.5,
  "full": false
}
```

| Status | Description |
| :--- | :--- |
| 200 OK | La requête s'est effectuée correctement |
| 404 NOT FOUND | Point de collecte introuvable |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Vider un point de collecte
**DELETE** `/points/{id}/clear`

Marque tous les dépôts du point comme collectés (`collecte = true`). Le taux de remplissage retombe à 0%. Les dépôts sont conservés en base pour le calcul du score.

| Status | Description |
| :--- | :--- |
| 204 NO CONTENT | Le point a été vidé avec succès |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Utilisation incorrecte (format attendu : `/points/{id}/clear`) |

---

#### Lister les points surchargés
**GET** `/points/overloaded`

Retourne les points dont le taux de remplissage dépasse 80%.

| Status | Description |
| :--- | :--- |
| 200 OK | Liste retournée (vide si aucun point surchargé) |

---

#### Récupérer tous les types de déchets
**GET** `/waste-types`

| Status | Description |
| :--- | :--- |
| 200 OK | Liste retournée |

---

#### Récupérer un type de déchet
**GET** `/waste-types/{id}`

| Status | Description |
| :--- | :--- |
| 200 OK | Type de déchet retourné |
| 404 NOT FOUND | ID non existant |

---

#### Ajouter un type de déchet
**POST** `/waste-types`

```json
{ "id": 6, "name": "Verre", "pointsPerKilo": 2 }
```

| Status | Description |
| :--- | :--- |
| 201 CREATED | Le type de déchet a été ajouté |
| 400 BAD REQUEST | Erreur de syntaxe JSON ou d'insertion |

---

#### Modifier un type de déchet
**PUT** `/waste-types/{id}`

```json
{ "name": "Verre recyclé", "pointsPerKilo": 3 }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Type mis à jour |
| 404 NOT FOUND | ID non existant |

---

#### Supprimer un type de déchet
**DELETE** `/waste-types/{id}`

| Status | Description |
| :--- | :--- |
| 204 NO CONTENT | Suppression réussie |
| 404 NOT FOUND | ID non existant |
| 409 CONFLICT | Le type est utilisé dans la table des dépôts |

---

#### Lister tous les dépôts
**GET** `/deposits`

Retourne les dépôts enrichis avec le nom du déchet et l'adresse du point (pas les IDs bruts).

| Status | Description |
| :--- | :--- |
| 200 OK | Liste retournée |

---

#### Récupérer un dépôt
**GET** `/deposits/{id}`

| Status | Description |
| :--- | :--- |
| 200 OK | Dépôt retourné |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Enregistrer un dépôt
**POST** `/deposits`

```json
{ "userId": 2, "pointId": 1, "wasteTypeId": 3, "poids": 5.5 }
```

| Status | Description |
| :--- | :--- |
| 201 CREATED | Dépôt enregistré |
| 400 BAD REQUEST | Poids négatif ou nul |
| 403 FORBIDDEN | Point de collecte saturé |

---

#### Modifier un dépôt (remplacement complet)
**PUT** `/deposits/{id}`

```json
{ "userId": 2, "pointId": 1, "wasteTypeId": 3, "poids": 7.0, "collecte": false }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Dépôt mis à jour |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Modifier partiellement un dépôt
**PATCH** `/deposits/{id}`

```json
{ "poids": 8.0 }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Champ(s) mis à jour |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Classement des recycleurs
**GET** `/users/leaderboard`

Retourne les 10 meilleurs utilisateurs triés par score décroissant. `score = Σ(poids × pointsPerKilo)`.

| Status | Description |
| :--- | :--- |
| 200 OK | Classement retourné |

---

#### Modifier un utilisateur (remplacement complet)
**PUT** `/users/{id}`

```json
{ "login": "paul", "password": "pass", "role": "USER" }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Utilisateur mis à jour |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |

---

#### Modifier partiellement un utilisateur
**PATCH** `/users/{id}`

```json
{ "password": "newpass" }
```

| Status | Description |
| :--- | :--- |
| 200 OK | Champ(s) mis à jour |
| 404 NOT FOUND | ID non existant |
| 400 BAD REQUEST | Identifiant invalide |



### 4. Schéma de la base de données

```
WasteType(id PK, name, pointsPerKilo)
CollectionPoint(id PK, adresse, capaciteMax)
accepts(pointId FK→CollectionPoint, wasteTypeId FK→WasteType)
users(id PK, login UNIQUE, password, role)
Deposit(id SERIAL PK, userId FK→users, pointId FK→CollectionPoint,
        wasteTypeId FK→WasteType, poids, dateDepot, collecte)
```

### 5. Configuration
La connexion à la base de données s'effectue via le fichier de propriétés `config.psql.prop` situé dans `WEB-INF/classes/`.