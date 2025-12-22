## 🚀 Lancer la stack complète (Docker)

L’ensemble de l’application (frontend Angular, backend Spring Boot après y avoir appliqué le traitement pour injecter les logs, MySQL, OpenTelemetry Collector et Jaeger) est orchestré via Docker Compose.

## Générer le backend instrumenté runnable

Le backend à exécuter est celui généré après instrumentation.

Depuis ce projet (`spoon-instrumenter`) :

```bash
mvn -q -DskipTests exec:java \
  -Dexec.mainClass=com.tp.instrumenter.InstrumenterMain \
  -Dexec.args="/path/to/productmanagement /path/to/dest/workspace/productmanagement-instrumented-runnable"
```

### Prérequis
- Docker
- Docker Compose

### Démarrage

Les dossiers `frontend` et `backend` doivent se trouver dans le même répertoire racine.
⚠️ Le backend à exécuter est **ce projet instrumenté** (pas le repo `productmanagement` original).

Placez-vous dans le dossier `backend` puis exécutez :

```bash
docker compose down
docker compose build --no-cache frontend
docker compose up -d
```

### Services exposés

- Frontend Angular : http://localhost:4200

- Backend API : http://localhost:8080

- Jaeger UI : http://localhost:16686

