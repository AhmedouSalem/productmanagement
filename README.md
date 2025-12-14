# 🛒 Product Management API

Secure REST API for managing **users**, **categories**, and **products**, built with  
**Spring Boot**, **Spring Security (JWT)**, and a layered architecture  
(Controller / Service / Repository).

This project was developed in an academic context (TP Logging & Observability)  
while following **professional best practices**.

---

## 📦 Project Structure (Important)

This work is composed of **three distinct projects**:

1. **productmanagement**  
   → Original Spring Boot application (no instrumentation).

2. **spoon-instrumenter**  
   → Standalone Java project using **Spoon** to automatically inject logging statements.

3. **productmanagement-instrumented-runnable**  
   → Runnable version of the application generated automatically after instrumentation.

---

## 🚀 Features

### 👤 Users
- Public user creation
- Authentication using email + password
- JWT generation on login

### 🔐 Security
- Stateless authentication using JWT
- All business endpoints are protected
- Current user identification via `SecurityContextHolder`

### 📦 Categories
- Full CRUD operations

### 🛍️ Products
- Full CRUD operations
- Retrieve products by category
- Retrieve **most expensive products** (global and per category)

---

## 🧱 Architecture

```
com.obs.productmanagement
├── controller
├── service
│   ├── impl
│   └── interfaces
├── repository
├── model
├── dto
│   ├── request / response
│   └── mapper
├── security
├── exception
└── ProductmanagementApplication
```

---

## ⚙️ Technologies

- Java 17+
- Spring Boot 3
- Spring Web / Spring Data JPA
- Spring Security + JWT
- Hibernate Validator
- H2 / MySQL
- Lombok / MapStruct
- JUnit 5 / Mockito
- Logback (JSON logging)

---

## ▶️ Run the application

```bash
mvn spring-boot:run
```

Application available at:

```
http://localhost:8080
```

---

## 🔑 Authentication (JWT)

### Create a user (PUBLIC)

```bash
curl -X POST http://localhost:8080/api/users   -H "Content-Type: application/json"   -d '{
        "name": "Salem",
        "age": 25,
        "email": "salem@example.com",
        "password": "secret123"
      }'
```

### Login (PUBLIC)

```bash
curl -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{
        "login": "salem@example.com",
        "password": "secret123"
      }'
```

Response:

```json
{
  "token": "<JWT_TOKEN>"
}
```

---

## 📂 Categories (JWT required)

```bash
curl -X POST http://localhost:8080/api/categories   -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{
        "name": "Electronics",
        "description": "Electronic devices"
      }'
```

---

## 🛒 Products (JWT required)

```bash
curl -X POST http://localhost:8080/api/products   -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{
        "name": "MacBook Pro",
        "description": "Laptop",
        "price": 2499.99,
        "expiryDate": "2026-01-01T00:00:00.000+00:00",
        "categoryId": 1
      }'
```

---

## 📊 Logging & Observability

- Service-layer methods are automatically instrumented using **Spoon**
- Logs are generated in **JSON format**
- Only application logs are persisted (framework logs are filtered out)
- Log file location:

```
logs/app.jsonl
```

Each log entry contains:
- event type (DB_READ, DB_WRITE, MOST_EXPENSIVE_SEARCH)
- service class and method
- user identifier

---

## ▶️ Scenarios execution (Q4)

A shell script is provided to automatically:
- create 10 users
- authenticate them
- execute different usage scenarios (READ / WRITE / MOST_EXPENSIVE)
```bash
chmod +x run-scenarios.sh
```

```bash
./run-scenarios.sh
```

---

## 📈 Log analysis & profiling (Q5)

A lightweight Python script is provided to analyse logs and identify user profiles.

```bash
python3 analyze_logs.py
```

Example output:

```
userId=2 counts={'DB_READ': 9} profile=DB_READ
userId=7 counts={'DB_WRITE': 2} profile=DB_WRITE
userId=10 counts={'MOST_EXPENSIVE_SEARCH': 5} profile=MOST_EXPENSIVE_SEARCH
```

---

## 🧪 Tests

```bash
mvn test
```

- Unit tests: Service layer
- JPA tests: Repository layer
- Application startup test

---

## 👨‍🎓 Author

**Ahmedou Salem**  
Master Informatique – Génie Logiciel  
Université de Montpellier

---

## 📄 License

Educational project – academic use only.