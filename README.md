# URL Shortener

A Spring Boot service that converts long URLs into short codes.\
Users register/login (JWT), create/manage links, and get redirects.

------------------------------------------------------------------------

## 🚀 Tech Stack

-   **Language/Build:** Java 24 (Toolchain), Gradle\
-   **Framework:** Spring Boot 3.5.5\
-   **Starters:** Web, Security, Data JPA\
-   **DB:** PostgreSQL (prod), H2 (dev option)\
-   **Migrations:** Flyway (core + postgres)\
-   **Docs:** Swagger annotations (UI wiring TBD)\
-   **CI/CD:** GitHub Actions (planned)

------------------------------------------------------------------------

## 📦 Project Info

-   **Group:** `org.decepticons`\
-   **Artifact:** `linkShortener`\
-   **Version:** `0.0.1-SNAPSHOT`

------------------------------------------------------------------------

## ⚙️ Setup

### 1) Requirements

-   JDK not required locally (Gradle uses **Java 24 toolchain**).
-   PostgreSQL for persistence (H2 available at runtime for quick
    tests).

### 2) Clone

``` bash
git clone https://github.com/RuslanLomaka/GoitJavaDevProjLinkShortener.git
cd GoitJavaDevProjLinkShortener
```

### 3) Environment Variables

Create `.env` in project root (not committed):

    DB_URL=jdbc:postgresql://localhost:5432/url_shortener
    DB_USERNAME=postgres
    DB_PASSWORD=postgres

    # Security
    JWT_SECRET=change-me
    JWT_TTL_SECONDS=3600

    # App
    APP_BASE_URL=http://localhost:8080
    SPRING_PROFILES_ACTIVE=default

> Devs keep their own `.env`. Never commit secrets.

------------------------------------------------------------------------

## ▶️ Run (Local)

### Option A: Plain run

``` bash
./gradlew bootRun
```

### Option B: With Docker Compose (dev only)

You have `spring-boot-docker-compose` (developmentOnly). If you add a
`compose.yaml` with Postgres, Spring Boot will auto-start it on
`bootRun`.

# Developer Quick Links (local)

When you run the app locally, these endpoints are available out-of-the-box:

| Feature            | URL                                           | Description                     | Notes |
|--------------------|-----------------------------------------------|---------------------------------|-------|
| Health check       | [http://localhost:8080/health](http://localhost:8080/api/v1/health) | Simple liveness endpoint.       | Returns `200 OK` when the app is up. |
| Swagger UI         | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) | Interactive API docs & try-it-out. | No auth required. |
| OpenAPI JSON       | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Raw OpenAPI spec (JSON).        | Useful for codegen/tools. |
| OpenAPI YAML       | [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) | Raw OpenAPI spec (YAML).        | Some tools prefer YAML. |
| H2 Console *(dev)* | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | In-memory DB web console.       | **Driver:** `org.h2.Driver`  • **JDBC URL:** `jdbc:h2:mem:shortenerdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`  • **User:** `sa`  • **Password:** *(empty)* |

> ℹ️ If you’ve changed the server port or context path, replace `http://localhost:8080/` with your actual base URL.  
> 🔐 H2 console is intended for **dev** usage only.


------------------------------------------------------------------------

## 🧪 Testing

-   Frameworks: JUnit 5, Mockito, Spring Security Test.\
-   Run tests:

``` bash
./gradlew test
```

> Recommend adding **Testcontainers** later for Postgres integration
> tests and a coverage gate (≥80%).

------------------------------------------------------------------------


# Admin / Health

The application provides a health check endpoint to verify that the service is running.

## Endpoint
```
GET http://localhost:8080/health
```

## Example Response
```json
{
  "status": "UP"
}
```

## Usage
- Open the endpoint in a web browser, Postman, or curl.
- A successful response with `"status": "UP"` confirms that:
    - The Spring Boot application started correctly.
    - The web server is accessible.
- This check is required for all team members to confirm before continuing with development.

## Notes
- The `/health` endpoint is unsecured and intended for internal verification only.
- Documented for project setup validation, not for production monitoring.


------------------------------------------------------------------------

## 🚀 Running with PostgreSQL (prod profile)
### 1. Install PostgreSQL
- On Windows: [Download installer](https://www.postgresql.org/download/windows/).
- During install, you will set a password for the **postgres superuser** — this is for *you only*, do not put it in the project.
### 2. Create database & app user
After install, open **SQL Shell (psql)** or **pgAdmin** and run these statements **once**:
```sql
-- Create the project database
CREATE DATABASE link_shortener;
-- Create a dedicated user for the app (replace 'url_pass' with your own password)
CREATE USER url_user WITH PASSWORD 'url_pass';
-- Give that user full access to the database
GRANT ALL PRIVILEGES ON DATABASE link_shortener TO url_user;
-- Make url_user the owner (optional but clean)
ALTER DATABASE link_shortener OWNER TO url_user;
```
### 👉 Important:
`url_pass` here is the password you will put into `.env` (not the superuser password).
Every developer can choose their own `url_pass` locally.
The app will never use the postgres superuser account.
### 3. Configure .env
Add these variables to your local .env file (they are already used by application.yml):
```DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=url_shortener
   DB_USERNAME=url_user
   DB_PASSWORD=url_pass
   DB_DRIVER_PROD=org.postgresql.Driver
   HIBERNATE_DIALECT_PROD=org.hibernate.dialect.PostgreSQLDialect
```
### 4. How it works
When you start with the prod profile, the app will connect using the values above.
Flyway will automatically run all scripts from src/main/resources/db/migration/postgresql
→ tables are created on first run, no manual CREATE TABLE needed.
### 5. Run the app

 Run with the prod profile
```cmd
./gradlew bootRun --args='--spring.profiles.active=prod'
```

 Connect to DB with psql -U url_user -d url_shortener -h localhost and run \dt to see created tables.

------------------------------------------------------------------------

## 🔐 Security

-   Spring Security starter included.\
-   Plan: register/login returning **JWT**; protect `/api/v1/**` except
    redirects.\
-   Store only **password hashes** (BCrypt/Argon2).

------------------------------------------------------------------------

## 🗃️ Database & Migrations

-   **Flyway** is included.\
-   Place migrations under:\
    `src/main/resources/db/migration`\
-   First migration (planned): create `users`, `links` tables with
    constraints.

------------------------------------------------------------------------

## 🧰 Lombok
We use [Lombok](https://projectlombok.org/) to reduce boilerplate code.
- Provides `@Getter`, `@Setter`, `@Data`, etc.
- Make sure to enable annotation processing in your IDE.

------------------------------------------------------------------------

## 📚 API (v1 Draft)

### Auth

-   `POST /api/v1/auth/register` --- create user
-   `POST /api/v1/auth/login` --- returns JWT

### User

-   `GET /api/v1/users/me` --- current profile
-   `PATCH /api/v1/users/me/password` --- change password

### Links

-   `POST /api/v1/links` --- create short link (`originalUrl`,
    `expiresAt?`)
-   `GET /api/v1/links` --- list own links (paging)
-   `GET /api/v1/links/{id}` --- details
-   `PATCH /api/v1/links/{id}` --- update URL/expiry/status
-   `DELETE /api/v1/links/{id}` --- delete

### Redirect (public)

-   `GET /{code}` --- 302 → original, updates stats

------------------------------------------------------------------------

## 🧾 OpenAPI / Swagger

-   You already use `io.swagger.core.v3:swagger-annotations`.\
-   To serve docs/UI later, add (suggestion):
    -   `org.springdoc:springdoc-openapi-starter-webmvc-ui`\
        Then docs at `/v3/api-docs`, UI at `/swagger-ui/index.html`.

------------------------------------------------------------------------

## 🧰 Useful Gradle Tasks

``` bash
./gradlew clean build      # build jar + run tests
./gradlew bootRun          # run app (uses toolchain)
./gradlew test             # unit/integration tests
```

------------------------------------------------------------------------

## 🧭 Conventions

-   Package: `org.decepticons.linkshortener` (suggested)\
-   Profiles: `default` (local), `prod` (server)\
-   Versioned API base: `/api/v1`\
-   Auth header: `Authorization: Bearer <token>`\
-   Error model: `{timestamp,status,error,message,path,traceId}`

------------------------------------------------------------------------

## 🗺️ Roadmap (Sprint 1)

1.  Flyway `V1__init.sql` --- `users`, `links` schema\
2.  Entities + repositories (User, Link)\
3.  Auth: register/login (JWT)\
4.  Create link + Redirect endpoints\
5.  Swagger UI wiring (springdoc)\
6.  docker-compose (app + Postgres)\
7.  Basic CI (build + test)

------------------------------------------------------------------------
