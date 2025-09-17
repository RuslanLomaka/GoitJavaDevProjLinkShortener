# Link Shortener

A Spring Boot service that turns long URLs into short codes. Users can register, log in (JWT), create/manage links, and anyone can be redirected by short code.

---

## Requirements
- Docker (Desktop/Engine) with **Compose v2**
- Git
- (Optional) JDK 21 for local IDE runs

---

## Environment
A sample file **`.env.example`** is included. Create your local env once:
```bash
cp .env.example .env
```
- For IDE/local runs, `.env` defaults to `SPRING_PROFILES_ACTIVE=dev` (H2).
- In Docker, **docker-compose** sets `SPRING_PROFILES_ACTIVE=prod` and injects Postgres settings for the app container.
- Keep real secrets (e.g., `JWT_SECRET`) only in your local `.env`.

**Variables used by the app**
- `SPRING_PROFILES_ACTIVE` — `dev` (H2) or `prod` (PostgreSQL)
- **dev / H2**: `DB_URL`, `DB_DRIVER`, `DB_USERNAME`, `DB_PASSWORD`
- **prod / PostgreSQL**: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME_PROD`, `DB_PASSWORD_PROD`
- Optional overrides (take precedence if set): `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

---

## Run with Docker (app + its own PostgreSQL)
Build and start both containers:
```bash
docker compose up --build
```
What you get:
- **app** on `http://localhost:8080` (profile **prod**)
- **db**: `postgres:16-alpine` with database **url_shortener**, user **url_user**, password **url_pass**
- Persistent volume **pgdata**
- Flyway runs migrations for PostgreSQL on startup

Useful:
```bash
# Stop containers (keep volume)
docker compose down

# Reset database (remove volume)
docker compose down -v

# psql into DB
docker exec -it linkshortener-db psql -U url_user -d url_shortener
```

---

## Run in IDE (dev profile, H2)
```bash
./gradlew bootRun
# Windows:
# gradlew.bat bootRun
```
- H2 console: `http://localhost:8080/h2-console`
- Swagger UI: `http://localhost:8080/swagger-ui`  
  Use the selector to switch between **api-v1** and **public** docs.

OpenAPI JSON:
- `/v3/api-docs/api-v1`
- `/v3/api-docs/public`

---

## API Endpoints

### Authentication (api-v1)
| Method | Path                     | Description                 |
|:------:|--------------------------|-----------------------------|
| POST   | `/api/v1/auth/register`  | Register a new user         |
| POST   | `/api/v1/auth/login`     | Log in and get JWT tokens   |
| POST   | `/api/v1/auth/refresh`   | Refresh access token        |
| POST   | `/api/v1/auth/logout`    | Logout and revoke token     |

### Links (api-v1, requires JWT)
| Method | Path                                 | Description               |
|:------:|--------------------------------------|---------------------------|
| POST   | `/api/v1/links`                      | Create a short link       |
| GET    | `/api/v1/links/my_all_links`         | List all your links       |
| GET    | `/api/v1/links/my_all_active_links`  | List only active links    |
| DELETE | `/api/v1/links/delete/{id}`          | Delete link by id         |

### Cache (api-v1)
| Method | Path              | Description |
|:------:|-------------------|-------------|
| GET    | `/api/v1/cache`   | Cache info  |

### Public
| Method | Path                 | Description             |
|:------:|----------------------|-------------------------|
| GET    | `/health`            | Health check            |
| GET    | `/api/links/{code}`  | Redirect by short code  |

---
## Deployment

The project is automatically deployed to a remote server via **GitHub Actions**.  
Every merge/push to the `master` branch triggers the workflow (`.github/workflows/deploy.yml`) which:

1. Connects to the server (`user@ip` via SSH)
2. Executes the `deploy.sh` script
3. Updates the repository (`git pull origin master`)
4. Rebuilds Docker containers and restarts the application (`docker compose up -d --build`)

**Note:** Private keys for GitHub Actions are stored in GitHub Secrets and **are not published** in the README.

---

## Postman Collections

There are two collections in the `postman/` folder:

- `Linkshortener_local_postman_collection.json` – for local version (`http://localhost:8080`)
- `Linkshortener_deployed_postman_collection.json` – for the production server (`http://91.225.7.129:8080`)

To use the collections, import them into Postman.

---

## Swagger (Production)

API documentation for the production server is available here:  
[Go to Swagger](http://91.225.7.129:8080/swagger-ui/index.html)


---

## What this app can do
- Shorten any valid URL and redirect by short code
- Manage your own links (create, list, delete, see active ones)
- JWT-based authentication and token refresh
- Database migrations via Flyway (H2 for dev, PostgreSQL for prod)
- Dockerized local setup (app + DB)

---

## Build (fat JAR)
```bash
./gradlew clean bootJar -x checkstyleMain -x checkstyleTest
```
Output: `build/libs/*.jar`

Runtime images:
- Builder: `gradle:8.14.3-jdk21`
- Runtime: `eclipse-temurin:21-jre`
