# 🎯 Motus Game - API & Frontend

Un jeu de mots inspiré de Motus avec une API Spring Boot et un frontend React/Vite.

## 🎮 Aperçu

Le jeu Motus est un jeu de mots où les joueurs doivent deviner un mot secret en un nombre limité d'essais. Chaque tentative donne des indices sur les lettres correctes et leur position.

### Fonctionnalités

- 🔐 **Authentification JWT** - Connexion sécurisée
- 🎲 **Mots aléatoires** - Base de données de mots avec difficultés
- 📊 **Système de scores** - Historique des parties
- 🎨 **Interface moderne** - React avec Tailwind CSS
- 📱 **Responsive** - Compatible mobile et desktop

## 🛠 Technologies

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** - Authentification JWT
- **Spring Data JPA** - ORM
- **H2/PostgreSQL** - Base de données
- **Maven** - Gestion des dépendances
- **Lombok** - Réduction du code boilerplate

### Frontend
- **React 18**
- **Vite** - Build tool moderne
- **JavaScript ES6+**
- **CSS3** - Styles personnalisés
- **Fetch API** - Requêtes HTTP

## 🏗 Architecture

```
motus-game/
├── backend/                 # API Spring Boot
│   ├── src/main/java/
│   │   ├── config/         # Configuration (Security, CORS)
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # Entités JPA
│   │   ├── repository/     # Repositories Spring Data
│   │   ├── service/        # Logique métier
│   │   └── web/            # Controllers REST
│   └── src/main/resources/
│       ├── application.yml # Configuration
│       └── data.sql        # Données initiales
└── frontend/               # Application React
    ├── src/
    │   ├── components/     # Composants React
    │   ├── pages/          # Pages principales
    │   ├── services/       # Services API
    │   └── styles/         # Styles CSS
    └── public/             # Assets statiques
```

## 🚀 Installation

### Prérequis

- **Java 17+**
- **Node.js 18+**
- **Maven 3.8+**
- **Git**

### 1. Cloner le Projet

```bash
git clone https://github.com/votre-username/motus-game.git
cd motus-game
```

### 2. Backend (API Spring Boot)

```bash
cd backend

# Compiler et lancer
mvn clean install
mvn spring-boot:run

# L'API sera disponible sur http://localhost:8090
```

### 3. Frontend (React)

```bash
cd frontend

# Installer les dépendances
npm install

# Lancer en mode développement
npm run dev

# L'application sera disponible sur http://localhost:5173
```

## ⚙️ Configuration

### Backend Configuration

#### `application.yml`
```yaml
server:
  port: 8090

spring:
  datasource:
    url: jdbc:h2:mem:motusdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
  h2:
    console:
      enabled: true
      path: /h2-console

jwt:
  secret: your-secret-key-here
  expiration: 86400000 # 24 heures
```

### Frontend Configuration

#### Variables d'Environnement (`.env`)
```env
VITE_API_BASE_URL=http://localhost:8090
VITE_APP_NAME=Motus Game
```

## 🔌 API Endpoints

### Authentification
```http
POST /api/auth/login
POST /api/auth/register
POST /api/auth/refresh
```

### Jeu
```http
GET  /api/game/word/random          # Obtenir un mot aléatoire
GET  /api/game/words/length/{length} # Mots par longueur
POST /api/game/score                # Sauvegarder un score
GET  /api/game/scores/user/{userId} # Scores d'un utilisateur
```

### Exemple de Requête

```javascript
// Obtenir un mot aléatoire
fetch('http://localhost:8090/api/game/word/random', {
  headers: {
    'Authorization': 'Bearer YOUR_JWT_TOKEN',
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

### Format des Réponses

#### WordDTO
```json
{
  "id": 1,
  "word": "MAISON",
  "length": 6,
  "difficulty": "EASY"
}
```

#### ScoreDTO
```json
{
  "id": 1,
  "score": 600,
  "dateTime": "2025-07-30T00:04:05",
  "username": "testuser",
  "userId": 1,
  "wordId": 4,
  "word": "MAISON",
  "difficulty": "EASY",
  "wordLength": 6,
  "isWon": true,
  "attempts": null
}
```

## 🎨 Frontend

### Structure des Composants

```
src/
├── components/
│   ├── GameBoard.jsx       # Plateau de jeu principal
│   ├── GameGrid.jsx        # Grille des lettres
│   ├── Keyboard.jsx        # Clavier virtuel
│   └── ScoreDisplay.jsx    # Affichage des scores
├── pages/
│   ├── Login.jsx           # Page de connexion
│   ├── Game.jsx            # Page de jeu
│   └── Scores.jsx          # Page des scores
└── services/
    └── api.js              # Service API
```

### Styles

Le projet utilise un système de CSS custom avec variables pour la cohérence :

```css
:root {
  --primary: #2563eb;
  --correct-color: #16a34a;
  --present-color: #eab308;
  --absent-color: #64748b;
  --background: #f8fafc;
}
```

## 🔧 Développement

### Scripts Disponibles

#### Backend
```bash
mvn clean install      # Compiler
mvn test               # Tests
mvn spring-boot:run    # Lancer l'API
```

#### Frontend
```bash
npm run dev            # Mode développement
npm run build          # Build production
npm run preview        # Prévisualiser le build
npm run lint           # Linter
```

### Tests

#### Backend
```bash
mvn test
```

#### Frontend
```bash
npm run test
```

## 🐛 Résolution des Problèmes

### Problèmes Courants

#### 1. Erreur CORS
```yaml
# Dans application.yml
cors:
  allowed-origins: http://localhost:5173
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
```

#### 2. JWT Token Expiré
```javascript
// Vérifier l'expiration du token
const token = localStorage.getItem('token');
if (isTokenExpired(token)) {
  // Rediriger vers login
  window.location.href = '/login';
}
```

#### 3. Références Circulaires JSON
✅ **Résolu** - Le projet utilise des DTOs pour éviter les références circulaires entre les entités.

#### 4. Base de Données H2
```bash
# Accéder à la console H2
http://localhost:8090/h2-console

# JDBC URL: jdbc:h2:mem:motusdb
# Username: sa
# Password: (vide)
```

### Logs de Debug

#### Backend
```java
// Ajouter dans application.yml
logging:
  level:
    net.salim.api_motus: DEBUG
    org.springframework.security: DEBUG
```

#### Frontend
```javascript
// Activer les logs API
localStorage.setItem('debug', 'true');
```

## 📈 Performance

### Backend
- Utilisation de DTOs pour réduire la taille des réponses
- Lazy loading sur les relations JPA
- Cache des mots fréquemment utilisés

### Frontend
- Code splitting avec React.lazy()
- Optimisation des re-renders avec useMemo/useCallback
- Compression des assets en production

## 🚀 Déploiement

### Backend (Production)
```bash
mvn clean package -DskipTests
java -jar target/motus-api-1.0.0.jar
```

### Frontend (Production)
```bash
npm run build
# Servir le dossier dist/ avec nginx ou Apache
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazin
