# TEST_PLAN — ProductManagement API (Sprint QA)

## Contexte
Campagne de tests visant à valider les endpoints REST de ProductManagement avec authentification JWT.

## Périmètre testé
- Auth/Users : login, create user, me
- Categories : CRUD
- Products : CRUD + endpoints spécifiques (most-expensive, by-category)

## Approche
- Priorisation P0/P1 :
    - P0 : parcours critiques + sécurité + validations
    - P1 : endpoints spécifiques + structure d’erreurs + cas avancés
- Automatisation :
    - RestAssured : suite d’intégration API
    - Login automatique pour récupérer le JWT
- Exécution :
    - Locale via `mvn test`
    - CI via pipeline (push/PR)

## Environnement
- Base URL : http://localhost:8080
- DB : H2 (runtime) ou MySQL (runtime) selon profil actif
- Pré-requis :
    - API démarrée
    - DB accessible

## Matrice sécurité (issue de SecurityConfig)
Public :
- POST /api/users
- POST /api/auth/login
  Protégé :
- Tous les autres endpoints /api/**

## Livrables
- TEST_CASES.md (P0/P1)
- Suite de tests automatisés (JUnit5 + RestAssured)
- Rapport de tests (Surefire)
- Pipeline CI (GitHub Actions/Jenkins)

## Planning (exemple week-end)
- J1 : finalisation TEST_CASES + squelette RestAssured + 8–10 tests P0
- J2 : compléter P0 + ajouter P1 + CI + README d’exécution