# LifeOS

LifeOS is an opinionated, executive-grade personal operating system designed to bridge high-level vision, tactical execution, habit tracking, and continuous personal growth into a cohesive workflow.

---

## 1. Repository Structure

```
LifeOS/
├── backend/                  # Spring Boot 3.x (Java 17+) REST API service
│   ├── src/main/java/com/lifeos/
│   │   ├── common/           # Shared entities, DTOs, exceptions, and global advice
│   │   ├── config/           # CORS, Security, JPA auditing configurations
│   │   ├── controller/       # REST API controllers (e.g., HealthController)
│   │   └── dto/              # Request/Response Data Transfer Objects
│   ├── src/main/resources/   # application.yml, db/migration (Flyway SQL scripts)
│   └── src/test/             # Unit and integration test suites (H2 in-memory MySQL mode)
├── frontend/                 # React 19 + Vite single-page web application
│   ├── src/
│   │   ├── components/       # Design system primitives and navigation shells
│   │   ├── design-system/    # Design tokens (tokens.css) & global styles
│   │   ├── layouts/          # Responsive AppLayout (Desktop sidebar + Mobile nav)
│   │   ├── pages/            # FoundationPage (Phase 1 verification surface)
│   │   └── services/         # Axios API client (/api/v1) with credentials & CSRF handling
│   └── package.json
├── database/                 # Database initialization and migration documentation
│   ├── schema.sql            # Minimal database setup (Flyway is authoritative schema manager)
│   └── README.md             # Migration conventions & guidelines
├── docs/                     # Canonical source-of-truth documents
│   ├── prd.md                # WHAT: Product Requirements Document
│   ├── architecture.md       # HOW: Architectural Blueprint & Decisions
│   ├── rules.md              # CONSTRAINTS: Engineering Standards & Invariants
│   └── design.md             # LOOK, FEEL & BEHAVIOR: Design System Specifications
└── README.md                 # Project overview and local execution guide
```

---

## 2. Architecture & Tech Stack

- **Backend:**
  - Java 17+ (Oracle JDK 23 compatible)
  - Spring Boot 3.3.6
  - Spring Data JPA + Hibernate (`validate` mode)
  - Spring Security (Stateless, server-managed HttpOnly cookie architecture, CSRF protection)
  - Flyway Database Migrations (Authoritative schema management)
  - MySQL 8.0 (Production / Local Runtime) & H2 (In-memory MySQL compatibility mode for unit/integration tests)
- **Frontend:**
  - React 19 (SPA)
  - Vite build toolchain
  - Vanilla CSS Design System with CSS Custom Properties (Tokens)
  - Axios API client (`/api/v1` namespace, `withCredentials: true`)
  - React Router DOM v7
- **Design System:**
  - Theme: Graphite / Midnight (`#0F172A`, `#1E293B`) foundation with Deep Indigo (`#4F46E5`) primary accent.
  - Semantic: Emerald (`#10B981`) for completed/success, Amber (`#F59E0B`) for warnings, Crimson (`#EF4444`) for critical items.
  - Spacing scale: 4px to 96px based on a 4px/8px modular rhythm.
  - Responsive Shell: Desktop 260px collapsible sidebar, mobile collapsible header and sticky bottom navigation bar (< 768px).

---

## 3. Environment Variables & Configuration

The backend reads configuration from environment variables with sensible local defaults:

| Variable | Description | Default |
| :--- | :--- | :--- |
| `SERVER_PORT` | Backend HTTP port | `8080` |
| `DB_HOST` | MySQL hostname | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_NAME` | Database name | `lifeos` |
| `DB_USER` | Database username | `root` |
| `DB_PASSWORD` | Database password | *(empty)* |
| `CORS_ALLOWED_ORIGINS` | Permitted frontend origins | `http://localhost:5173,http://localhost:3000` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |

---

## 4. Local Development Setup

### Prerequisites
- Java 17 or higher
- Node.js v18+ and npm v9+
- MySQL 8.0+

### Database Setup
1. Ensure your MySQL server is running locally on port 3306.
2. Initialize the database schema:
   ```sql
   CREATE DATABASE IF NOT EXISTS lifeos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Application tables and schema changes are managed automatically on startup via Flyway migrations (`db/migration/V*.sql`).

### Backend Execution
Navigate to the `backend` directory:
```bash
cd backend

# Run automated tests
./mvnw clean test

# Package application jar
./mvnw clean package

# Run local Spring Boot server
./mvnw spring-boot:run
```
The backend will boot up at `http://localhost:8080`.
The public health verification endpoint is available at:
```
GET http://localhost:8080/api/v1/health
```

### Frontend Execution
Navigate to the `frontend` directory:
```bash
cd frontend

# Install dependencies
npm install

# Start local development server with HMR
npm run dev

# Run production build
npm run build
```
The frontend will start at `http://localhost:5173`.

---

## 5. Phase 1 Implementation Status

- [x] **Repository Directory Structure:** Aligned with `architecture.md`.
- [x] **Source of Truth Documents:** Consolidated and reconciled in `docs/`.
- [x] **Flyway Migration Foundation:** Initial baseline migration `V1__init_baseline.sql` configured with Hibernate schema validation.
- [x] **Backend Skeleton:** Spring Boot 3.3.6 with Java 17+, BaseEntity auditing, RFC-7807 GlobalExceptionHandler, SecurityConfig, and `/api/v1/health`.
- [x] **Automated Tests:** Comprehensive unit and integration test suite passing with in-memory H2.
- [x] **Frontend Architecture:** Vite + React 19 app with complete CSS design system tokens, responsive `AppLayout` (desktop sidebar + mobile navigation), component primitives, and `FoundationPage` health verification.
- [x] **Feature Boundary Verification:** Strict adherence to Phase 1 foundation boundaries (zero business entities, zero authentication implementation, zero premature scaling infrastructure).
