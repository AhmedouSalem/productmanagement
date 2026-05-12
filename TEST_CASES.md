# TEST_CASES — ProductManagement API (P0/P1)
_Source: Swagger/OpenAPI (`/v3/api-docs`)_

## 1) Périmètre (endpoints)
> Codes déclarés sur toutes les opérations : **200, 400, 404, 409, 500**

### Auth / Users
- POST `/api/auth/login`
- POST `/api/users`
- GET  `/api/users/me`

**Sécurité (SecurityConfig)**
- Public : POST /api/users, POST /api/auth/login
- Protégé : toute autre route /api/** (attendu 401 sans token)

### Categories
- GET    `/api/categories`
- POST   `/api/categories`
- GET    `/api/categories/{id}`   (id >= 1)
- PUT    `/api/categories/{id}`   (id >= 1)
- DELETE `/api/categories/{id}`   (id >= 1)

### Products
- GET    `/api/products`
- POST   `/api/products`
- GET    `/api/products/{id}`     (id >= 1)
- PUT    `/api/products/{id}`     (id >= 1)
- DELETE `/api/products/{id}`     (id >= 1)
- GET    `/api/products/most-expensive`
- GET    `/api/products/by-category/{categoryId}`                      (categoryId > 0)
- GET    `/api/products/by-category/{categoryId}/most-expensive`       (categoryId > 0)

## 2) Modèles & validations (DTO)
### LoginRequest
- required: login, password
- minLength: login >= 1, password >= 1

### UserCreateRequest
- required: name, age, email, password
- constraints: name minLength 1, age >= 12, email format email, password minLength 1

### CategoryRequest
- required: name, description
- constraints: name minLength 1, description minLength 1

### ProductRequest
- required: categoryId, description, expiryDate, name, price
- constraints: name minLength 1, description minLength 1, price >= 0, expiryDate format date-time

### ErrorResponse
- message, status, error (présent sur les erreurs)

## 3) Auth (JWT)
- Schéma: **Bearer JWT** (`bearerAuth`)
- Remarque: la spec ne tagge pas les endpoints protégés → à confirmer via SecurityConfig.
- Dans les tests, prévoir :
    - sans token → **401/403** (si endpoint protégé)
    - avec token → **200**

## 4) Cas de tests P0 (indispensables)
### AUTH
- TC-AUTH-01 (P0) Login OK
    - GIVEN user existant
    - WHEN POST /api/auth/login (login + password valides)
    - THEN 200 + token non vide

- TC-AUTH-02 (P0) Login KO champs manquants
    - WHEN POST /api/auth/login avec login vide OU password vide
    - THEN 400 + ErrorResponse

### USERS
- TC-USR-01 (P0) Create user OK
    - WHEN POST /api/users avec name, age>=12, email valide, password
    - THEN 200 + UserResponse (id présent)

- TC-USR-02 (P0) Create user KO email invalide / age < 12
    - THEN 400 + ErrorResponse

- TC-USR-03 (P0) Me (avec token)
    - WHEN GET /api/users/me avec Authorization: Bearer <token>
    - THEN 200 + UserResponse

- TC-USR-04 (P0) Me (sans token) [si protégé]
    - WHEN GET /api/users/me sans header Authorization
    - THEN 401/403

### CATEGORIES (CRUD)
- TC-CAT-01 (P0) Create category OK
    - WHEN POST /api/categories (name, description non vides)
    - THEN 200 + CategoryResponse

- TC-CAT-02 (P0) Create category KO (name/description vides)
    - THEN 400 + ErrorResponse

- TC-CAT-03 (P0) Get category by id OK / 404
    - WHEN GET /api/categories/{id} avec id existant → 200
    - WHEN id inexistant → 404

- TC-CAT-04 (P0) Param id invalide
    - WHEN GET/PUT/DELETE /api/categories/0
    - THEN 400 (id >= 1)

- TC-CAT-05 (P0) Update category OK / 404 / 400 validation
- TC-CAT-06 (P0) Delete category OK / 404

### PRODUCTS (CRUD)
- TC-PRD-01 (P0) Create product OK
    - Précondition: categoryId existant
    - WHEN POST /api/products (categoryId, name, description, price>=0, expiryDate)
    - THEN 200 + ProductResponse

- TC-PRD-02 (P0) Create product KO (champs requis manquants)
    - THEN 400 + ErrorResponse

- TC-PRD-03 (P0) Get product list
    - WHEN GET /api/products
    - THEN 200 + tableau

- TC-PRD-04 (P0) Get product by id OK / 404
- TC-PRD-05 (P0) Update product OK / 404 / 400 validation
- TC-PRD-06 (P0) Delete product OK / 404
- TC-PRD-07 (P0) Param id invalide
    - WHEN GET/PUT/DELETE /api/products/0
    - THEN 400 (id >= 1)

## 5) Cas de tests P1 (bonus très valorisant)
- TC-PRD-11 (P1) most-expensive
    - WHEN GET /api/products/most-expensive
    - THEN 200 + produit(s) au prix max (dataset contrôlé)

- TC-PRD-12 (P1) products by categoryId
    - WHEN GET /api/products/by-category/{categoryId} avec categoryId existant (>0)
    - THEN 200 + uniquement produits de la catégorie

- TC-PRD-13 (P1) most-expensive by categoryId
    - WHEN GET /api/products/by-category/{categoryId}/most-expensive
    - THEN 200 + max de la catégorie

- TC-ERR-01 (P1) Vérifier structure ErrorResponse sur 400/404
    - THEN message/status/error cohérents

- TC-SEC-01 (P1) Accès sans token aux endpoints protégés
    - THEN 401/403