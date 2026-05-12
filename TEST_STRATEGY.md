# TEST_STRATEGY — ProductManagement API

## Objectifs
- Vérifier la conformité fonctionnelle des endpoints REST.
- Sécuriser l’API (JWT) : accès public vs protégé.
- Prévenir les régressions (CRUD produits/catégories + endpoints spécifiques).
- Valider les contraintes de validation (DTO) et la gestion d’erreurs (ErrorResponse).

## Périmètre
Inclus :
- Tests d’API (fonctionnels / intégration) sur :
    - Auth / Users : /api/auth/login, /api/users, /api/users/me
    - Products : /api/products/** (CRUD + endpoints "most-expensive" / "by-category")
    - Categories : /api/categories/**
- Tests négatifs : 400/404 + structure d’erreur
- Sécurité : 401 sans token sur endpoints protégés

Hors périmètre (pour ce sprint/week-end) :
- UI / Front (Selenium), performance lourde, tests de charge, contract testing avancé.
- Rôles/403 (non implémentés actuellement).

## Types de tests
- Smoke tests : endpoints critiques (login + list products/categories).
- Tests fonctionnels API (black-box) : codes HTTP + payloads + règles métier.
- Tests de validation : champs requis, minLength, price >= 0, age >= 12, email format.
- Tests sécurité :
    - Public : POST /api/users, POST /api/auth/login (sans token)
    - Protégé : tout le reste (401 sans token)

## Outils
- Automatisation : JUnit 5 + RestAssured (référence) / (option) Postman + Newman.
- Build : Maven (`mvn test`).
- (Optionnel) Base de données test : H2 / Testcontainers (si besoin d’isolation).

## Environnements
- Local : Spring Boot + DB dev/test.
- CI : exécution automatique des tests sur chaque push (GitHub Actions ou Jenkins).

## Données de test
- Données minimales contrôlées :
    - 1 utilisateur de test
    - 1 catégorie
    - 2–3 produits (dont 1 “plus cher”)
- Nettoyage : suppression via endpoints DELETE ou reset DB (selon stratégie retenue).

## Critères d’entrée / sortie
Entrée :
- API démarrable, Swagger OK, endpoints accessibles.
  Sortie :
- 0 échec P0
- Rapport de tests généré (Surefire) et pipeline CI vert.

## Risques & mitigation
- Dépendance à l’état de la DB → fixer des données de seed et nettoyer.
- JWT instable (token expiré) → login automatique au début des tests.