# 🛒 Product Management API

API REST sécurisée pour la gestion des **utilisateurs**, **catégories** et **produits**, développée avec **Spring Boot**, **Spring Security (JWT)** et une architecture en couches (Controller / Service / Repository).

Ce projet est conçu dans un cadre pédagogique (TP Logging & Observability) mais respecte les **bonnes pratiques professionnelles**.

---

## 🚀 Fonctionnalités

### 👤 Utilisateurs

* Création d’un utilisateur (endpoint public)
* Authentification par email ou nom + mot de passe
* Génération d’un **JWT** au login

### 🔐 Sécurité

* Authentification stateless avec **JWT**
* Protection de tous les endpoints métiers
* Identification de l’utilisateur courant via `SecurityContextHolder`

### 📦 Catégories

* CRUD catégories

### 🛍️ Produits

* CRUD produits
* Récupérer tous les produits d’une catégorie
* Récupérer les produits **les plus chers** (globalement)
* Récupérer les produits **les plus chers par catégorie**

### 🧪 Tests

* Tests unitaires sur la **couche Service** (Mockito)
* Tests JPA sur la **couche Repository** (`@DataJpaTest`)
* Test de démarrage Spring Boot (`@SpringBootTest`)

---

## 🧱 Architecture

```
com.obs.productmanagement
├── controller
│   ├── AuthController
│   ├── UserAuthController
│   ├── CategoryController
│   └── ProductController
├── service
│   ├── impl
│   └── interfaces
├── repository
├── model (entities JPA)
├── dto
│   ├── request / response
│   └── mapper (MapStruct)
├── security
│   ├── JwtService
│   ├── JwtAuthenticationFilter
│   ├── SecurityConfig
│   └── SecurityUtils
├── exception
│   ├── custom exceptions
│   └── GlobalExceptionHandler
└── ProductmanagementApplication
```

---

## ⚙️ Technologies

* Java 17+
* Spring Boot 3
* Spring Web
* Spring Data JPA
* Spring Security
* JWT (jjwt)
* Hibernate Validator
* H2 / MySQL
* Lombok
* MapStruct
* JUnit 5 / Mockito

---

## ▶️ Lancer le projet

### 1️⃣ Cloner le projet

```bash
git clone <repository-url>
cd product-management
```

### 2️⃣ Lancer l’application

```bash
mvn spring-boot:run
```

Application disponible sur :

```
http://localhost:8080
```

---

## 🔑 Authentification (JWT)

### ➜ Créer un utilisateur (PUBLIC)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Salem",
        "age": 25,
        "email": "salem@example.com",
        "password": "secret123"
      }'
```

### ➜ Login (PUBLIC)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
        "login": "salem@example.com",
        "password": "secret123"
      }'
```

Réponse :

```json
{
  "token": "<JWT_TOKEN>",
  "user": {
    "id": 1,
    "name": "Salem",
    "email": "salem@example.com"
  }
}
```

Stocker le token :

```bash
TOKEN="<JWT_TOKEN>"
```

---

## 📂 Catégories (JWT requis)

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Electronics",
        "description": "Electronic devices"
      }'
```

---

## 🛒 Produits (JWT requis)

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "MacBook Pro",
        "description": "Laptop",
        "price": 2499.99,
        "expiryDate": "2026-01-01T00:00:00.000+00:00",
        "categoryId": 1
      }'
```

---

## 🔍 Recherche avancée

### ➜ Produits les plus chers (global)

```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/products/most-expensive
```

### ➜ Produits d’une catégorie

```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/products/by-category/1
```

### ➜ Produits les plus chers par catégorie

```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/products/by-category/1/most-expensive
```

---

## 🧪 Tests

Lancer tous les tests :

```bash
mvn test
```

* Tests unitaires : couche **Service**
* Tests JPA : couche **Repository**
* Test de démarrage : `@SpringBootTest`

---

## 📊 Logging & Observability (prévu)

* Logs contextualisés (userId, email, operation)
* Préparation pour OpenTelemetry / Grafana

---

## 👨‍🎓 Auteur

Projet réalisé par **Ahmedou Salem**
Master Informatique – Génie Logiciel
Université de Montpellier

---

## 📄 Licence

Projet pédagogique – usage académique.
