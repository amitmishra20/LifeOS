# LifeOS --- System Architecture

**Version:** 1.0\
**Status:** Architecture Baseline\
**Purpose:** Production-oriented architecture for LifeOS, designed for
real users rather than only a college demonstration.

## 1. Architecture Vision

LifeOS is a Personal Operating System that helps users turn long-term
goals into daily actions, track tasks and habits, monitor learning and
progress, and receive explainable recommendations.

The architecture follows five principles:

1.  User data is private by default.
2.  React never connects directly to MySQL.
3.  Business rules live in the backend.
4.  The initial production architecture is a **modular monolith**, not
    premature microservices.
5.  The backend is designed so it can be scaled horizontally as usage
    grows.

Core flow:

``` text
User
  ↓
Responsive React Frontend
  ↓
HTTPS / REST API
  ↓
Spring Boot Backend
  ↓
Controller
  ↓
Service / Business Logic
  ↓
Repository / JPA
  ↓
MySQL
  ↓
Response back to React
```

------------------------------------------------------------------------

## 2. High-Level Architecture

``` mermaid
flowchart LR
    U[User Devices] --> FE[React Responsive Frontend]
    FE -->|HTTPS / REST / JSON| BE[Spring Boot Backend]
    BE --> DB[(MySQL)]

    BE --- AUTH[Authentication & Authorization]
    BE --- REC[Recommendation Engine]
    BE --- ANA[Analytics]
    BE --- AUD[Audit & Security Logging]
```

### Responsibilities

  -----------------------------------------------------------------------
  Component                           Responsibility
  ----------------------------------- -----------------------------------
  User/device                         Interacts with LifeOS

  React                               UI, navigation, forms, client state
                                      and responsive experience

  REST API                            Contract between frontend and
                                      backend

  Controller                          Receives requests and returns
                                      responses

  Service                             Business rules and workflows

  Repository                          Database access

  MySQL                               Persistent storage

  Security                            Authentication, authorization and
                                      ownership enforcement

  Recommendation Engine               Explainable prioritization and
                                      Daily Focus

  Analytics                           Progress and productivity metrics

  Audit                               Security-sensitive event tracking
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 3. Architectural Style --- Modular Monolith

The first production version should be a **modular monolith**.

``` text
LifeOS Spring Boot Application
│
├── Authentication & Security
├── Users
├── Goals
├── Milestones
├── Tasks
├── Habits
├── Learning
├── Calendar
├── Notes
├── Analytics
├── Recommendation Engine
├── Notifications
└── Audit
```

There is one deployable backend, but its internal modules have clear
boundaries.

### Why not start with microservices?

Microservices add distributed communication, deployment complexity,
monitoring overhead, network failures and distributed data concerns.

For LifeOS, starting with microservices would add complexity before
there is a proven need.

The preferred evolution is:

``` text
Modular Monolith
      ↓
Measure real bottlenecks
      ↓
Optimize database / queries
      ↓
Horizontal scaling
      ↓
Add Redis / queues where justified
      ↓
Extract only high-load modules if necessary
```

Possible future extraction candidates:

-   AI / recommendation processing
-   Notifications
-   Analytics processing
-   Search
-   Background workers

------------------------------------------------------------------------

## 4. Frontend Architecture

### Technology

-   React
-   Vite
-   React Router
-   Axios or equivalent HTTP client
-   Tailwind CSS
-   Recharts or equivalent chart library
-   Form validation

### Structure

``` text
src/
├── components/
├── pages/
├── layouts/
├── services/
├── hooks/
├── context/
├── utils/
├── types/
└── assets/
```

Frontend responsibilities:

-   Render the UI.
-   Handle navigation.
-   Validate user input at the UI level.
-   Call REST APIs.
-   Show loading/error/success states.
-   Adapt to mobile, tablet and desktop.
-   Never contain database credentials or server secrets.
-   Never make authoritative authorization decisions.

------------------------------------------------------------------------

## 5. Responsive Architecture

Responsive design is a first-class requirement.

LifeOS must work on:

-   Mobile phones
-   Tablets / iPads
-   Laptops
-   Desktop monitors

The UI must not simply shrink the desktop version.

``` text
Mobile ─┐
Tablet ─┼──> Responsive React UI ──> Same REST APIs
Laptop ─┤
Desktop ┘
```

### Mobile

-   Touch-friendly controls
-   Compact navigation
-   Stacked cards
-   Readable charts
-   Mobile-friendly forms/modals
-   No accidental horizontal page scrolling

### Tablet

-   Collapsible navigation
-   Adaptive grids
-   Reduced dashboard density

### Desktop

-   Sidebar navigation
-   Multi-column dashboard
-   Larger analytics views
-   Full calendar experience

The backend remains device-independent.

------------------------------------------------------------------------

## 6. Backend Layered Architecture

``` text
HTTP Request
     ↓
Security Filters
     ↓
Controller
     ↓
DTO Validation
     ↓
Service
     ↓
Business Rules
     ↓
Repository / JPA
     ↓
MySQL
```

Response:

``` text
MySQL
  ↓
Repository
  ↓
Service
  ↓
Response DTO
  ↓
Controller
  ↓
JSON
  ↓
React
```

### Controller

The Controller is the HTTP entry point.

It should:

-   Define endpoints.
-   Receive requests.
-   Validate request structure.
-   Delegate work.
-   Return appropriate HTTP responses.

Controllers should remain thin.

### Service

The Service layer contains business logic.

Example:

``` text
TaskService.completeTask(userId, taskId)

1. Find task
2. Verify task exists
3. Verify ownership
4. Apply completion rules
5. Save changes
6. Update related progress if required
7. Return safe DTO
```

### Repository

The Repository handles persistence using Spring Data JPA/Hibernate.

It should query and save data efficiently, while authorization decisions
remain in the service/security layer.

------------------------------------------------------------------------

## 7. Core Data Model

Main entities:

``` text
User
 │
 ├── Goals
 │     └── Milestones
 │            └── Tasks
 │
 ├── Tasks
 ├── Habits
 │     └── Habit Logs
 ├── Learning Items
 │     └── Learning Sessions
 ├── Calendar Events
 └── Notes
```

Conceptual relationships:

``` mermaid
erDiagram
    USER ||--o{ GOAL : owns
    USER ||--o{ TASK : owns
    USER ||--o{ HABIT : owns
    USER ||--o{ LEARNING_ITEM : owns
    USER ||--o{ CALENDAR_EVENT : owns
    USER ||--o{ NOTE : owns
    GOAL ||--o{ MILESTONE : contains
    GOAL ||--o{ TASK : supports
    HABIT ||--o{ HABIT_LOG : has
    LEARNING_ITEM ||--o{ LEARNING_SESSION : contains
```

Every user-owned record must have a reliable ownership path back to the
authenticated user.

------------------------------------------------------------------------

## 8. Privacy and User Data Isolation

This is a core architecture rule.

Never trust a user ID supplied by the frontend.

Bad:

``` text
GET /api/tasks?userId=2
```

if the backend blindly trusts `userId`.

Correct:

``` text
JWT / authenticated session
        ↓
Authenticated User = 17
        ↓
Backend derives identity from security context
        ↓
Query resources owned by User 17
```

Example:

``` sql
SELECT *
FROM tasks
WHERE id = ?
AND user_id = authenticatedUserId;
```

This rule applies to:

-   Goals
-   Milestones
-   Tasks
-   Habits
-   Learning
-   Calendar
-   Notes
-   Analytics
-   Future personal data

A user must never be able to access or modify another user's records by
changing an ID in the request.

------------------------------------------------------------------------

## 9. Authentication and Authorization

### Authentication

Answers:

> Who is this user?

### Authorization

Answers:

> What is this user allowed to access?

Protected request:

``` text
Client
  ↓
HTTPS + Authentication
  ↓
Security Filter
  ↓
Validate identity
  ↓
Controller
  ↓
Service
  ↓
Ownership check
  ↓
Database
```

### Password security

Passwords must never be stored in plain text.

Use a strong password hashing algorithm such as BCrypt or Argon2 through
the security framework.

``` text
Password
  ↓
Strong password hash
  ↓
Database
```

------------------------------------------------------------------------

## 10. Token / Session Security

LifeOS uses secure server-managed authentication via HttpOnly cookies:

-   HttpOnly authentication cookies prevent script access and mitigate XSS-based token theft.
-   `Secure` flag enabled in production; strict/lax `SameSite` policy.
-   CSRF protection implemented where applicable.
-   Authentication tokens are never stored in browser `localStorage`.
-   Password hashes and authentication secrets are never exposed through DTOs or logs.
-   Server-side authorization is strictly enforced on every request; frontend route guards provide UX routing only.
-   The backend remains stateless and horizontally scalable, allowing any backend instance behind a load balancer to validate the cookie/token.


------------------------------------------------------------------------

## 11. API Security

Production request path:

``` text
HTTPS
 ↓
Authentication
 ↓
Authorization
 ↓
Input Validation
 ↓
Ownership Check
 ↓
Business Rules
 ↓
Database
```

Additional controls:

-   Restrict CORS to trusted frontend origins.
-   Rate-limit sensitive endpoints.
-   Protect login from brute-force abuse.
-   Enforce request-size limits.
-   Validate all input.
-   Avoid exposing stack traces or SQL errors.
-   Use secure response headers where appropriate.
-   Keep dependencies updated and scanned.

------------------------------------------------------------------------

## 12. DTO Boundary

Do not expose database entities directly by default.

``` text
Database Entity
      ↓
Service
      ↓
Response DTO
      ↓
Controller
      ↓
JSON
```

DTOs help prevent:

-   Password hash exposure
-   Accidental sensitive-field leakage
-   Database schema leakage
-   Over-fetching
-   Uncontrolled API contracts

For example, `passwordHash` must never appear in a normal User API
response.

------------------------------------------------------------------------

## 13. REST API Design

All application APIs are standardized under the `/api/v1/` prefix:

``` text
Authentication & Users
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/logout
GET    /api/v1/auth/me
DELETE /api/v1/users/me

Goals
GET    /api/v1/goals
GET    /api/v1/goals/{id}
POST   /api/v1/goals
PUT    /api/v1/goals/{id}
DELETE /api/v1/goals/{id}

Milestones
GET    /api/v1/goals/{goalId}/milestones
POST   /api/v1/goals/{goalId}/milestones
GET    /api/v1/milestones/{id}
PUT    /api/v1/milestones/{id}
DELETE /api/v1/milestones/{id}

Tasks
GET    /api/v1/tasks
GET    /api/v1/tasks/{id}
POST   /api/v1/tasks
PUT    /api/v1/tasks/{id}
DELETE /api/v1/tasks/{id}
PATCH  /api/v1/tasks/{id}/complete

Habits
GET    /api/v1/habits
GET    /api/v1/habits/{id}
POST   /api/v1/habits
PUT    /api/v1/habits/{id}
DELETE /api/v1/habits/{id}
POST   /api/v1/habits/{id}/logs

Learning
GET    /api/v1/learning
GET    /api/v1/learning/{id}
POST   /api/v1/learning
PUT    /api/v1/learning/{id}
DELETE /api/v1/learning/{id}
POST   /api/v1/learning/{id}/sessions

Calendar Events
GET    /api/v1/events
GET    /api/v1/events/{id}
POST   /api/v1/events
PUT    /api/v1/events/{id}
DELETE /api/v1/events/{id}

Notes
GET    /api/v1/notes
GET    /api/v1/notes/{id}
POST   /api/v1/notes
PUT    /api/v1/notes/{id}
DELETE /api/v1/notes/{id}

Analytics
GET    /api/v1/analytics/dashboard
GET    /api/v1/analytics/productivity
GET    /api/v1/analytics/goals
GET    /api/v1/analytics/habits

Recommendations
GET    /api/v1/recommendations
GET    /api/v1/recommendations/daily-focus
```


------------------------------------------------------------------------

## 14. Recommendation Engine

The MVP uses a deterministic, rule-based, explainable recommendation engine.
Recommendations and Daily Focus are dynamic and stateless, computed on demand
by `RecommendationService` from current real data (no persistent recommendations
table or Redis cache in MVP).

Inputs:

-   Task priority
-   Deadline proximity
-   Goal health and importance
-   Overdue status
-   Completion state (`status IN ('TODO', 'IN_PROGRESS', 'OVERDUE')`)

Deterministic scoring formula:

``` text
Task Score = Priority Score + Deadline Score + Goal Health Score + Overdue Score
```

Centralized scoring constants:

``` text
Priority Score:
  LOW       = 10
  MEDIUM    = 20
  HIGH      = 30
  CRITICAL  = 40

Deadline Score:
  Due today       = +40
  Due tomorrow    = +30
  Due in 2 days   = +20
  Due in 3-7 days = +10
  Due > 7 days    = +0

Overdue Score:
  Overdue         = +50

Goal Health Score:
  Linked goal BEHIND  = +30
  Linked goal AT_RISK = +15
  Linked goal ON_TRACK= +0
  No linked goal      = +0
```

The engine ranks incomplete tasks and returns the top 5 highest-scoring tasks as
the user's Daily Focus.


### Explainability

Recommendations should tell the user why they were generated.

Example:

``` text
Focus on "Complete Java DSA"

Why?
- High-priority goal
- Deadline is approaching
- Task is incomplete
- Goal progress is behind target
```

This keeps the MVP understandable and trustworthy.

------------------------------------------------------------------------

## 15. Analytics Architecture

``` text
Tasks / Goals / Habits / Learning
              ↓
       Analytics Service
              ↓
          Metrics
              ↓
        REST API
              ↓
        React Dashboard
```

Possible metrics:

-   Goal progress
-   Task completion rate
-   Habit consistency
-   Habit streaks
-   Learning progress
-   Learning hours
-   Productivity score
-   Goal health

Analytics should avoid repeatedly scanning large datasets as the
application grows. Expensive calculations can later move to background
jobs or precomputed aggregates.

------------------------------------------------------------------------

## 16. Goal Health

Goal lifecycle status and goal health are strictly separated:

-   **Goal Status (Lifecycle):** `ACTIVE`, `PAUSED`, `COMPLETED`, `ARCHIVED`
-   **Goal Health (Evaluation):** `ON_TRACK`, `AT_RISK`, `BEHIND`, `COMPLETED`

`AT_RISK` and `BEHIND` are health states, never lifecycle statuses.

### Deterministic Health Model

``` text
Expected Progress = (elapsed time / total goal duration) * 100

If progress >= 100%:
    Health = COMPLETED

If progress >= (Expected Progress - 5%):
    Health = ON_TRACK

If progress < (Expected Progress - 5%) AND progress >= (Expected Progress - 25%):
    Health = AT_RISK

If progress < (Expected Progress - 25%) OR (targetDate < today AND progress < 100%):
    Health = BEHIND
```

These thresholds are centralized, configurable business rules in `GoalHealthService`.
Goal progress should primarily be derived from milestone/task completion where
practical, reducing conflicting manual values.


------------------------------------------------------------------------

## 17. Error Handling

Use a consistent API error structure:

``` json
{
  "timestamp": "2026-09-07T12:00:00Z",
  "status": 404,
  "code": "RESOURCE_NOT_FOUND",
  "message": "Task not found",
  "path": "/api/tasks/15"
}
```

Never expose to users:

-   Stack traces
-   SQL errors
-   Internal implementation details
-   Passwords
-   Tokens

Frontend users should receive clear human-readable messages.

------------------------------------------------------------------------

## 18. Database Performance

Start with a well-designed MySQL schema.

Consider indexes around real query patterns, for example:

``` text
tasks(user_id)
tasks(user_id, status)
tasks(user_id, deadline)
goals(user_id)
habits(user_id)
calendar_events(user_id, start_time)
notes(user_id)
```

Do not add indexes blindly. Verify them against actual queries and
workload.

Other controls:

-   Connection pooling
-   Pagination
-   Efficient joins
-   Avoid N+1 queries
-   Transactions for related writes
-   Query profiling for slow operations

------------------------------------------------------------------------

## 19. Horizontal Scalability

The backend should be as stateless as practical.

Instead of:

``` text
Users
  ↓
One Spring Boot Server
  ↓
MySQL
```

Production can scale to:

``` text
                         ┌── Spring Boot #1 ──┐
Users → Load Balancer ───┼── Spring Boot #2 ──┼──> MySQL
                         └── Spring Boot #N ──┘
```

Any healthy backend instance should be able to serve any request.

Do not keep critical user state only in one server's local memory.

------------------------------------------------------------------------

## 20. Scaling Roadmap

### Stage 1 --- Initial release

``` text
React Hosting
     ↓
Spring Boot
     ↓
Managed MySQL
```

### Stage 2 --- Growing user base

``` text
React
  ↓
Load Balancer
  ├── Backend #1
  ├── Backend #2
  └── Backend #3
          ↓
        MySQL
```

Add:

-   Query optimization
-   Indexes
-   Connection pooling
-   Monitoring
-   Rate limiting

### Stage 3 --- Higher read traffic

``` text
Backends
   ├── Redis (when justified)
   └── MySQL
        └── Read replicas (when justified)
```

### Stage 4 --- Large workloads

``` text
Core Application
   ├── Recommendation Worker
   ├── Analytics Worker
   ├── Notification Worker
   └── Search / AI Service
```

Only introduce these when measurements show a real need.

------------------------------------------------------------------------

## 21. Caching

Potential technology:

``` text
Redis
```

Use caching selectively for:

-   Frequently requested non-sensitive data
-   Expensive analytics results
-   Short-lived recommendation results

User-specific cache keys must include user identity.

Good:

``` text
recommendations:user:17:today
```

Dangerous:

``` text
recommendations:today
```

if the value is user-specific.

Cache invalidation and expiry must be deliberately designed.

------------------------------------------------------------------------

## 22. Background Processing

Heavy work should not block normal user requests.

Examples:

-   Daily recommendation refresh
-   Analytics aggregation
-   Notifications/reminders
-   Large exports
-   Future AI processing

Architecture:

``` text
Spring Boot
    ↓
Queue / Scheduler
    ↓
Worker
    ↓
Database / External Service
```

This allows the UI to remain responsive.

------------------------------------------------------------------------

## 23. Privacy Architecture

LifeOS contains personal productivity information that can become highly
sensitive.

Privacy principles:

### Data minimization

Collect only what is needed.

### Purpose limitation

Use data only for clearly defined product purposes.

### User isolation

Users access only their own data unless an explicit secure sharing
feature exists.

### Least privilege

Services and administrators should receive only the access required for
their jobs.

### Secure deletion & account deletion

Explicit user account deletion (`DELETE /api/v1/users/me`) triggers a controlled
transactional deletion of all user-owned application records:
- Goals & Milestones
- Tasks
- Habits & Habit Logs
- Learning Items & Learning Sessions
- Calendar Events
- Notes
- Productivity Data

The system relies on programmatic, controlled transactions rather than uncontrolled
blind cascading behavior across the database. Only security/audit logs genuinely
required for security, legal, or operational compliance are retained.


### Transparency

Users should understand:

-   What LifeOS stores.
-   Why it is stored.
-   Whether it is shared with third parties.
-   How data can be exported/deleted.

------------------------------------------------------------------------

## 24. Data Classification

A practical classification:

``` text
Public
  ↓
Account Data
  ↓
Personal Productivity Data
  ↓
Private/Sensitive Content
```

Examples of highly private LifeOS data:

-   Notes
-   Goals
-   Routines
-   Calendar information
-   Productivity patterns
-   Learning history

These must not be exposed through public URLs, search engines, logs, or
third-party analytics without an appropriate reason and user-facing
privacy controls.

------------------------------------------------------------------------

## 25. Security Threat Model

  -----------------------------------------------------------------------
  Threat                              Primary Protection
  ----------------------------------- -----------------------------------
  Unauthorized account access         Strong password hashing + secure
                                      authentication

  User A accessing User B data        Server-side authorization +
                                      ownership checks

  SQL injection                       JPA / parameterized queries

  XSS                                 Safe rendering + output encoding +
                                      CSP where appropriate

  CSRF                                Secure cookie strategy + CSRF
                                      protection where applicable

  Brute-force login                   Rate limiting / throttling

  Token theft                         HTTPS + secure token strategy

  Data leakage                        DTOs + safe errors + logging
                                      controls

  CORS abuse                          Explicit trusted origins

  Malicious input                     Validation + size limits

  Dependency vulnerabilities          Dependency scanning and updates

  Secret leakage                      Environment variables / secret
                                      manager

  Database compromise                 Restricted access + encryption +
                                      backups

  Privileged misuse                   Least privilege + audit logging
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 26. Secrets Management

Never commit secrets to Git.

Never hard-code:

``` text
DB_PASSWORD
JWT_SECRET
API_KEY
CLOUD_CREDENTIALS
```

Use:

``` text
Environment Variables
        or
Secret Manager
```

Keep development, staging and production credentials separate.

------------------------------------------------------------------------

## 27. Audit Logging

Record important security events such as:

``` text
Login success
Login failure
Password change
Account deletion
Sensitive setting changes
Administrative actions
Authorization failures
```

Never log:

``` text
Passwords
JWTs
Refresh tokens
Private note contents
Sensitive personal content
```

Logs should answer:

``` text
What happened?
When?
Which account?
Which resource?
Was it successful?
```

while minimizing personal information.

------------------------------------------------------------------------

## 28. Observability

Production monitoring should cover:

### Application

-   Request count
-   Error rate
-   p50/p95/p99 latency
-   Slow endpoints

### Database

-   Query latency
-   Connection pool usage
-   Slow queries
-   Error rates

### Infrastructure

-   CPU
-   Memory
-   Disk
-   Network

### Security

-   Failed-login spikes
-   Repeated authorization failures
-   Suspicious request patterns

Use centralized logs, metrics and alerting in production.

------------------------------------------------------------------------

## 29. Reliability

Assume components can fail.

Examples:

``` text
Database unavailable
API timeout
Server instance crashes
Network failure
Third-party service unavailable
```

The system should:

-   Use health checks.
-   Return controlled errors.
-   Use timeouts for external dependencies.
-   Retry only safe/idempotent operations.
-   Keep application instances replaceable.
-   Maintain database backups.
-   Use transactions for critical multi-step writes.
-   Define recovery procedures.

------------------------------------------------------------------------

## 30. Transactions

Use transactions when multiple changes must succeed or fail together.

Example:

``` text
Complete Milestone
       ↓
Update Milestone
       ↓
Recalculate Goal Progress
       ↓
Update Goal State
```

Conceptually:

``` text
BEGIN
  Update A
  Update B
  Update C
COMMIT
```

If a critical failure occurs:

``` text
ROLLBACK
```

This protects data consistency.

------------------------------------------------------------------------

## 31. Deployment Architecture

A production-oriented deployment can evolve into:

``` mermaid
flowchart LR
    USER[User Device]
    WEB[CDN / Frontend Hosting]
    LB[Load Balancer]

    A1[Spring Boot #1]
    A2[Spring Boot #2]
    AN[Spring Boot #N]

    REDIS[(Redis - Optional)]
    DB[(MySQL)]
    JOB[Background Workers]
    OBS[Monitoring / Centralized Logs]

    USER --> WEB
    USER --> LB
    LB --> A1
    LB --> A2
    LB --> AN

    A1 --> DB
    A2 --> DB
    AN --> DB

    A1 -.-> REDIS
    A2 -.-> REDIS
    AN -.-> REDIS

    A1 --> JOB
    A2 --> JOB
    AN --> JOB

    A1 --> OBS
    A2 --> OBS
    AN --> OBS
```

The first deployment can remain simple:

``` text
Frontend Hosting
       ↓
Spring Boot Backend
       ↓
Managed MySQL
```

The architecture should be capable of evolving without rewriting the
product.

------------------------------------------------------------------------

## 32. CI/CD

Recommended workflow:

``` text
Developer
   ↓
Git
   ↓
Pull Request
   ↓
Automated Tests
   ↓
Build
   ↓
Security / Dependency Checks
   ↓
Staging
   ↓
Smoke Tests
   ↓
Production
```

Use separate environments:

``` text
Development → Staging → Production
```

Production data should not casually be copied into development.

------------------------------------------------------------------------

## 33. Testing Strategy

### Unit tests

Test:

-   Services
-   Recommendation scoring
-   Goal health
-   Habit streaks
-   Validation rules

### Integration tests

Test:

``` text
Controller
   ↓
Service
   ↓
Repository
   ↓
Test Database
```

### Security tests

Verify:

-   Unauthenticated requests are rejected.
-   User A cannot read User B data.
-   User A cannot modify User B data.
-   Invalid/expired credentials are rejected.
-   Ownership checks work on every relevant resource.

### End-to-end test

Critical path:

``` text
Register
 ↓
Login
 ↓
Create Goal
 ↓
Create Task
 ↓
Complete Task
 ↓
Dashboard updates
 ↓
Recommendation changes
```

------------------------------------------------------------------------

## 34. Production Performance

Performance goals should be measured under realistic concurrent load.

Track:

-   p50 latency
-   p95 latency
-   p99 latency
-   Throughput
-   Error rate
-   Database load

Important optimizations:

``` text
Pagination
Efficient queries
Indexes
Connection pooling
Reduced payloads
Caching when justified
Background processing
```

The objective is predictable performance as concurrent users increase,
not merely fast performance on a developer laptop.

------------------------------------------------------------------------

## 35. Feature Development Workflow

Every new feature should follow:

``` text
Requirement
    ↓
Domain / Data Model
    ↓
API Contract
    ↓
Security / Ownership Rules
    ↓
Service / Business Logic
    ↓
Repository
    ↓
Controller
    ↓
Frontend API Service
    ↓
UI
    ↓
Tests
    ↓
Performance / Monitoring Review
```

This keeps new features consistent instead of turning LifeOS into random
CRUD modules.

------------------------------------------------------------------------

## 36. Complete Request Example --- Mark Task as Completed

``` text
1. User
   ↓
2. React UI
   ↓
3. PATCH /api/v1/tasks/15/complete
   ↓
4. HTTPS
   ↓
5. Security Filter
   ↓
6. Authenticate user
   ↓
7. TaskController
   ↓
8. TaskService
   ↓
9. Verify task belongs to authenticated user
   ↓
10. Apply business rules
   ↓
11. TaskRepository
   ↓
12. MySQL transaction
   ↓
13. Save completion
   ↓
14. Update related progress if required
   ↓
15. Return safe DTO
   ↓
16. React receives JSON
   ↓
17. UI updates
   ↓
18. Analytics / recommendations reflect the new state
```

------------------------------------------------------------------------

## 37. Daily Focus Example

``` text
User opens Dashboard
        ↓
React requests Daily Focus
        ↓
GET /api/v1/recommendations/daily-focus
        ↓
Security validates user
        ↓
Recommendation Service
        ↓
Fetch user's relevant tasks/goals
        ↓
Calculate:
  Priority
  Deadline urgency
  Goal importance/risk
  Overdue status
  Effort
        ↓
Rank tasks
        ↓
Generate explanation
        ↓
Return recommendations
        ↓
React displays Today's Focus
```

------------------------------------------------------------------------

## 38. Future AI Architecture

AI should be an isolated extension.

``` text
React
  ↓
Spring Boot
  ↓
AI / Assistant Service
  ↓
Controlled LifeOS Data Retrieval
  ↓
LLM Provider
  ↓
Response Validation / Safety Layer
  ↓
User
```

Important privacy rule:

> Private LifeOS data should not automatically be sent to an external AI
> provider.

Future AI features should use:

-   Data minimization
-   Explicit user controls/consent where appropriate
-   Provider-specific privacy controls
-   No secrets in prompts
-   Action validation
-   Auditability
-   Clear distinction between AI suggestions and authoritative system
    state

------------------------------------------------------------------------

## 39. Architecture Decisions

Important decisions should be documented as ADRs.

``` text
ADR-001: React for frontend
ADR-002: Spring Boot for backend
ADR-003: MySQL as primary database
ADR-004: Modular monolith for initial production architecture
ADR-005: Server-side ownership enforcement
ADR-006: Explainable rule-based recommendation engine for MVP
ADR-007: Stateless backend for horizontal scaling
ADR-008: Responsive design as a first-class requirement
ADR-009: Privacy and security by design
```

------------------------------------------------------------------------

## 40. Production Readiness Checklist

### Architecture

-   [ ] Clear frontend/backend separation
-   [ ] Layered backend
-   [ ] Modular boundaries
-   [ ] No direct frontend-to-database access
-   [ ] Stateless backend design

### Security

-   [ ] Strong password hashing
-   [ ] Secure authentication
-   [ ] Authorization
-   [ ] Ownership checks
-   [ ] HTTPS
-   [ ] Restricted CORS
-   [ ] Input validation
-   [ ] Rate limiting
-   [ ] Secret management
-   [ ] Safe error handling
-   [ ] Security logging

### Privacy

-   [ ] Data minimization
-   [ ] Clear privacy policy
-   [ ] User data isolation
-   [ ] Controlled third-party data sharing
-   [ ] Data deletion strategy
-   [ ] Backup retention strategy

### Scalability

-   [ ] Pagination
-   [ ] Efficient queries
-   [ ] Database indexes
-   [ ] Connection pooling
-   [ ] Horizontal backend scaling
-   [ ] Load balancing
-   [ ] Background jobs where required
-   [ ] Caching only where justified

### Reliability

-   [ ] Database backups
-   [ ] Health checks
-   [ ] Monitoring
-   [ ] Centralized logs
-   [ ] Error tracking
-   [ ] Recovery plan

### Product Quality

-   [ ] Mobile tested
-   [ ] Tablet tested
-   [ ] Laptop tested
-   [ ] Desktop tested
-   [ ] Critical workflows tested
-   [ ] Accessibility reviewed
-   [ ] Performance tested under realistic load

------------------------------------------------------------------------

# 41. Final Architecture Philosophy

LifeOS should be:

**Simple enough to build now.\
Structured enough to maintain.\
Secure enough to protect personal data.\
Modular enough to evolve.\
Stateless enough to scale horizontally.\
Explainable enough to earn user trust.**

The core architecture is:

``` text
                         USERS
          Mobile / Tablet / Laptop / Desktop
                              │
                              ▼
                  ┌─────────────────────┐
                  │   REACT FRONTEND    │
                  │   Responsive UI     │
                  └──────────┬──────────┘
                             │
                         HTTPS/REST
                             │
                             ▼
              ┌────────────────────────────┐
              │     SPRING BOOT BACKEND    │
              │      MODULAR MONOLITH      │
              │                            │
              │ Security                  │
              │ Controllers               │
              │ Services                  │
              │ Recommendations           │
              │ Analytics                 │
              │ Audit                     │
              │ Repositories              │
              └──────────────┬─────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │      MySQL      │
                    │   System of     │
                    │     Record      │
                    └─────────────────┘
```

For growth:

``` text
                         Load Balancer
                              │
             ┌────────────────┼────────────────┐
             ▼                ▼                ▼
        Backend #1       Backend #2       Backend #N
             │                │                │
             └────────────────┼────────────────┘
                              ▼
                     Shared Infrastructure
                       ┌──────┴──────┐
                       ▼             ▼
                     Redis*        MySQL
                                     │
                               Read Replicas*
```

`*` should be introduced only when real measurements justify them.

**Core principle:**

> Build the first version as a clean modular monolith, enforce privacy
> and authorization at the server, keep application servers stateless,
> and introduce distributed infrastructure only when real scale requires
> it.
