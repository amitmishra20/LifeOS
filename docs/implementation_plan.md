# Implementation Plan — Phase 1: Repository & Development Foundation (Revised)

Establish the clean repository structure, Spring Boot 3.x backend foundation, React Vite frontend foundation with design tokens and application shell, Flyway migration setup, and comprehensive documentation for LifeOS.

## User Review Required

> [!IMPORTANT]
> **Foundation-Only Scope**:
> - **No authentication logic**: No login, registration, UserDetailsService, auth filters, or session creation will be implemented in Phase 1.
> - **No feature-specific entities or tables**: No Goals, Milestones, Tasks, Habits, Learning, Calendar, Notes, Analytics, or Recommendations entities or tables.
> - **No artificial response envelopes**: No generic `ApiResponse<T>` wrapper. Endpoints return clean domain DTOs directly (e.g., `HealthResponse`), while errors use the standardized `ErrorResponse`.
> - **Flyway for Schema Migrations**: Hibernate `ddl-auto` is set to `validate`. Database schema is managed intentionally through Flyway migrations.
> - **Minimal Verification Surface**: `FoundationPage` is strictly a technical verification surface (verifying shell, tokens, and backend ping), not a placeholder dashboard.

---

## Proposed Changes

### 1. Repository Structure & Documentation

Move source-of-truth documents into `docs/` and establish top-level directory layout:

```
LifeOS/
├── frontend/
├── backend/
├── database/
├── docs/
│   ├── prd.md
│   ├── architecture.md
│   ├── rules.md
│   ├── design.md
│   └── (reference docx files)
└── README.md
```

#### [NEW] [README.md](file:///d:/lifeOS/README.md)
* Overview of LifeOS vision and architecture.
* Stack details: React (Vite), Spring Boot 3.x (Java 17+), MySQL 8.0, Flyway.
* Repository structure and directory navigation guide.
* Local development prerequisites (JDK 17+, Node 18+, MySQL 8.0).
* Step-by-step setup and run instructions for backend, frontend, and database.
* Environment variables catalog (`PORT`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`).
* Clear statement of current phase status (Phase 1 Foundation).

#### [NEW] [database/schema.sql](file:///d:/lifeOS/database/schema.sql)
* Minimal database creation script (`CREATE DATABASE IF NOT EXISTS lifeos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`).
* No speculative domain tables.

#### [NEW] [database/README.md](file:///d:/lifeOS/database/README.md)
* Database connection guide, MySQL 8.0 setup, user permissions, and Flyway migration workflow documentation.

---

### 2. Backend Foundation (`backend/`)

Initialize Spring Boot 3.4.x application using Java 17+ and the official Maven wrapper (`mvnw` / `mvnw.cmd`).

#### [NEW] [backend/pom.xml](file:///d:/lifeOS/backend/pom.xml)
* Spring Boot Starter Web
* Spring Boot Starter Data JPA
* Spring Boot Starter Security
* Spring Boot Starter Validation
* Flyway Core (`org.flywaydb:flyway-core`) & Flyway MySQL (`org.flywaydb:flyway-mysql`)
* MySQL Connector/J (`com.mysql:mysql-connector-j`)
* Project Lombok (optional utility for clean DTOs)
* Spring Boot Starter Test (JUnit 5 + MockMvc)

#### [NEW] [backend/src/main/resources/application.yml](file:///d:/lifeOS/backend/src/main/resources/application.yml)
* Environment-variable driven configuration without hardcoded secrets:
  * Server port: `${PORT:8080}`
  * DataSource URL: `jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:lifeos}?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
  * Username / Password: `${DB_USERNAME:root}` / `${DB_PASSWORD:}`
  * JPA `hibernate.ddl-auto: validate` (schema mutation handled strictly by Flyway)
  * Flyway: `enabled: true`, `locations: classpath:db/migration`

#### [NEW] [backend/src/main/resources/db/migration/V1__init_baseline.sql](file:///d:/lifeOS/backend/src/main/resources/db/migration/V1__init_baseline.sql)
* Minimal initial migration file. Contains no domain feature tables for Phase 1. Ensures Flyway schema history table is initialized cleanly.

#### [NEW] Base Entity & JPA Auditing:
* `com.lifeos.common.entity.BaseEntity` (MappedSuperclass providing `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`, `@CreatedDate Instant createdAt`, `@LastModifiedDate Instant updatedAt`).
* `com.lifeos.config.JpaConfig` (`@EnableJpaAuditing`).

#### [NEW] Global API Error Handling:
* `com.lifeos.common.dto.ErrorResponse` (RFC-7807 compatible error payload with `timestamp`, `status`, `code`, `message`, `path`, and `fieldErrors` map).
* `com.lifeos.common.exception.GlobalExceptionHandler` (`@RestControllerAdvice` handling `MethodArgumentNotValidException`, `ResourceNotFoundException`, `AccessDeniedException`, and unexpected exceptions).
* `com.lifeos.common.exception.ResourceNotFoundException`.

#### [NEW] Security Foundation (No Authentication Implemented):
* `com.lifeos.config.SecurityConfig`:
  * Establishes `SecurityFilterChain` bean.
  * Disables basic auth and form login defaults.
  * Configures stateless session management (`SessionCreationPolicy.STATELESS`).
  * Permits unauthenticated access to health endpoint (`GET /api/v1/health`).
  * Configures CSRF foundation (`CookieCsrfTokenRepository` with `HttpOnly=false` so the frontend can read the token for state-changing requests).
  * Prepares CORS configuration aligned with `CORS_ALLOWED_ORIGINS`.
* `com.lifeos.config.CorsConfig`:
  * Restricts allowed origins to trusted frontend URL (`${CORS_ALLOWED_ORIGINS:http://localhost:5173}`).
  * Enables credentials (`allowCredentials: true`) to support HttpOnly cookies.

#### [NEW] Health Controller & DTO:
* `com.lifeos.dto.HealthResponse` (Simple DTO returning `status`, `timestamp`, `version`).
* `com.lifeos.controller.HealthController` (`GET /api/v1/health` returning `HealthResponse` directly without custom envelope).

---

### 3. Frontend Foundation (`frontend/`)

Initialize Vite + React project.

#### [NEW] Design System Tokens & Global Styling:
* `frontend/src/design-system/tokens/tokens.css`:
  * Foundation surfaces: Neutral Graphite / Midnight (`--surface-bg: #0F172A`, `--surface-card: #1E293B`, `--surface-subtle: #334155`, `--surface-border: #334155`).
  * Primary Accent: Deep Indigo (`--color-accent: #4F46E5`, `--color-accent-hover: #4338CA`, `--color-accent-subtle: rgba(79, 70, 229, 0.1)`).
  * Semantic Colors: Emerald (`--color-success: #10B981`), Amber (`--color-warning: #F59E0B`), Crimson (`--color-danger: #EF4444`).
  * Spacing Scale: `4px, 8px, 12px, 16px, 24px, 32px, 48px, 64px, 96px`.
  * Radius Tokens: `small: 4px, medium: 8px, large: 12px, pill: 9999px`.
  * Shadow Tokens: subtle elevation levels (`--shadow-sm`, `--shadow-md`).
  * Motion Timing: `micro: 150ms, small: 220ms, drawer: 300ms, major: 450ms` with `@media (prefers-reduced-motion)` overrides.
* `frontend/src/index.css`: CSS reset, font declarations, and token imports.

#### [NEW] Reusable Primitives:
* `frontend/src/design-system/components/Button.jsx` (Primary, Secondary, Ghost variants using tokens).
* `frontend/src/design-system/components/Card.jsx` (Token-based surface container).
* `frontend/src/design-system/components/Badge.jsx` (Semantic status indicators).

#### [NEW] Application Shell & Navigation Layout:
* `frontend/src/layouts/AppLayout.jsx` (Responsive container managing desktop sidebar, tablet collapse, and mobile top/bottom bars).
* `frontend/src/components/navigation/Sidebar.jsx` (Implements the 6 intent groups from `design.md` §7: `HOME`, `FOCUS`, `PLAN`, `GROW`, `REFLECT`, `CAPTURE` + `Settings` / `Logout`).
* `frontend/src/components/navigation/MobileNav.jsx` (Compact mobile top header + bottom navigation bar).
* `frontend/src/components/navigation/Header.jsx` (Compact top header showing active section and backend connectivity badge).

#### [NEW] API Client Foundation:
* `frontend/src/services/api.js` (Axios client pointing to `/api/v1` with `withCredentials: true` for HttpOnly cookie support and unified response error handling).

#### [NEW] Minimal Verification Page:
* `frontend/src/pages/FoundationPage.jsx` (Strictly a development verification view: pings `GET /api/v1/health`, displays backend connection status, design token test swatch, and layout viewport indicators. No fake dashboards or placeholder productivity features).

---

## Verification Plan

### Automated Build & Tests
1. **Backend Maven Compilation:**
   ```powershell
   cd d:\lifeOS\backend
   .\mvnw.cmd clean compile
   ```
2. **Backend Unit & Context Tests:**
   ```powershell
   cd d:\lifeOS\backend
   .\mvnw.cmd test
   ```
3. **Frontend Production Build:**
   ```powershell
   cd d:\lifeOS\frontend
   npm run build
   ```

### Runtime Verification
1. **Backend Startup & Health Endpoint:**
   * Start backend: `.\mvnw.cmd spring-boot:run`
   * Test health endpoint: `curl -s http://localhost:8080/api/v1/health`
   * Confirm response:
     ```json
     {
       "status": "UP",
       "timestamp": "2026-09-22T...",
       "version": "1.0.0"
     }
     ```
   * Test 404 error format: `curl -s http://localhost:8080/api/v1/nonexistent`
   * Confirm RFC-7807 error structure with `code`, `message`, `status`, `path`.
2. **Flyway Migration Verification:**
   * Confirm Flyway initializes and validates schema without error on application boot.
3. **Frontend Runtime & Responsive Layout:**
   * Run frontend: `npm run dev`
   * Verify rendering at mobile (<640px), tablet (768px), and desktop (>1024px).
   * Verify sidebar renders the 6 confirmed navigation groups.
   * Verify health check badge shows "Connected (UP)".
4. **Secret & Scope Check:**
   * Audit all files to verify zero hardcoded credentials and zero domain feature entities/tables.
