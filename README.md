# Documentation de l'API REST - Gestion des Déchets

**Date :** 16 mars 2026
**Format supporté :** JSON, XML

### 1. Liste des points d'accès

| URI | Opération | MIME | Requête | Réponse |
| :--- | :--- | :--- | :--- | :--- |
| `/points` | GET | `<-application/json` `<-application/xml` | | Liste des points de collecte (21) |
| `/points/overloaded` | GET | `<-application/json` | | Liste des points saturés |
| `/points/{id}/status` | GET | `<-application/json` | | État de remplissage (22) |
| `/waste-types` | GET | `<-application/json` | | Liste des types de déchets (23) |
| `/waste-types` | POST | `->application/json` | Type de déchet (24) | Nouveau type ou 400 |
| `/users/leaderboard` | GET | `<-application/json` | | Classement des utilisateurs |



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

#### 2.2 Type de déchet (23)
Définit une catégorie de déchet et la valeur associée.
```json
{
  "id": 10,
  "name": "Plastique",
  "pointsPerKilo": 5
}
```

### 3. Opérations détaillées

#### Récupérer l'état d'un point
**GET** `/points/{id}/status`

**Requête vers le serveur**
`GET /points/1/status`

**Réponse du serveur**
```json
{
  "pointId": 1,
  "currentWeight": 450,
  "isOverloaded": false
}
```

| Status | Description |
| :--- | :--- |
| 200 OK | La requête s'est effectuée correctement |
| 404 NOT FOUND | Point de collecte introuvable |
| 400 BAD REQUEST | Identifiant invalide |



#### Ajouter un type de déchet
**POST** `/waste-types`

**Requête vers le serveur**
```json
{
  "name": "Verre",
  "pointsPerKilo": 2
}
```

| Status | Description |
| :--- | :--- |
| 201 CREATED | Le type de déchet a été ajouté |
| 400 BAD REQUEST | Erreur de syntaxe JSON ou d'insertion |



#### Vider un point de collecte
**DELETE** `/points/{id}/clear`

| Status | Description |
| :--- | :--- |
| 204 NO CONTENT | Le point a été vidé avec succès |
| 404 NOT FOUND | ID non existant |



### 4. Configuration
La connexion à la base de données s'effectue via le fichier de propriétés `config.psql.prop` situé dans `WEB-INF/classes/`.