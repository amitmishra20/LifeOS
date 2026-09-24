# LifeOS --- Engineering & Product Rules

**Document:** `rules.md`\
**Project:** LifeOS --- Your Life, Organized. Focused. Growing.\
**Status:** Living project constitution\
**Applies to:** Frontend, backend, database, APIs, infrastructure,
recommendation engine, analytics, security, privacy, and future AI
features.

------------------------------------------------------------------------

## 1. Purpose of This Document

This document defines the non-negotiable rules and engineering
guardrails for building and evolving LifeOS.

The purpose is to ensure that LifeOS remains:

-   Useful rather than feature-bloated
-   Secure and privacy-first
-   Scalable
-   Maintainable
-   Mobile-friendly
-   Explainable
-   Reliable
-   Consistent across frontend and backend
-   Suitable for real users, not only a college demonstration

### Relationship with Other Documents

  Document            Primary Question
  ------------------- ----------------------------------------------
  `prd.md`            What are we building and why?
  `architecture.md`   How is the system designed technically?
  `rules.md`          What rules must every implementation follow?

If another implementation conflicts with these rules, the implementation
must be reconsidered before it is merged.

------------------------------------------------------------------------

# 2. Core Product Rules

## 2.1 LifeOS is a Personal Operating System

LifeOS must not become just another:

-   To-do list
-   Calendar application
-   Habit tracker
-   Notes application
-   Productivity dashboard

Its purpose is to connect these areas into one coherent personal system.

A feature should contribute to at least one of these outcomes:

1.  Better organization
2.  Better prioritization
3.  Better execution
4.  Better understanding of progress
5.  Better decision-making
6.  Better alignment between daily actions and long-term goals

If a proposed feature does not provide meaningful value, it should not
be added simply because it is technically interesting.

------------------------------------------------------------------------

## 2.2 Avoid Feature Bloat

Before adding a feature, ask:

-   What user problem does it solve?
-   Which LifeOS workflow does it improve?
-   Is it necessary?
-   Can an existing feature solve the same problem?
-   Does it introduce significant complexity?
-   Does it affect privacy or security?
-   Does it work well on mobile?

Complexity must have a clear product benefit.

------------------------------------------------------------------------

## 2.3 User Control Comes First

LifeOS may recommend, prioritize, analyze, or highlight information, but
the user remains in control.

The system must not silently:

-   Delete user data
-   Change goals
-   Complete tasks
-   Change deadlines
-   Modify habits
-   Rewrite notes
-   Change important settings

unless the user explicitly authorizes the action.

------------------------------------------------------------------------

# 3. Architecture Rules

## 3.1 Respect the Defined Architecture

The initial architecture is a modular monolith:

``` text
React Frontend
       |
       v
Spring Boot REST API
       |
       +--> Authentication & Authorization
       +--> Goal Management
       +--> Task Management
       +--> Habit Management
       +--> Learning Management
       +--> Calendar
       +--> Notes
       +--> Analytics
       +--> Recommendation Engine
       |
       v
     MySQL
```

The system should remain modular internally even though it is deployed
as one backend application initially.

------------------------------------------------------------------------

## 3.2 Frontend Must Not Access the Database Directly

The React application must never communicate directly with MySQL.

Correct:

``` text
React
  ↓
REST API
  ↓
Spring Boot
  ↓
Repository
  ↓
MySQL
```

Incorrect:

``` text
React
  ↓
MySQL
```

All persistence must pass through the backend.

------------------------------------------------------------------------

## 3.3 Maintain Layer Separation

Backend responsibilities should remain separated:

``` text
Controller
    ↓
Service / Domain Logic
    ↓
Repository
    ↓
Database
```

### Controller

Controllers should:

-   Receive HTTP requests
-   Validate/request-bind input
-   Authenticate through the security layer
-   Call appropriate services
-   Return appropriate responses

Controllers should not contain large business rules.

### Service

Services should contain:

-   Business rules
-   Ownership checks where appropriate
-   State transitions
-   Recommendation calculations
-   Cross-entity operations
-   Transaction boundaries where needed

### Repository

Repositories should handle persistence and database access.

Database queries should not be scattered throughout controllers or
unrelated service classes.

------------------------------------------------------------------------

## 3.4 Do Not Introduce Microservices Prematurely

LifeOS should start as a modular monolith.

Do not split the application into microservices merely to make the
architecture look advanced.

Microservices should only be introduced when there is a measurable
reason such as:

-   Independent scaling requirement
-   Clear deployment independence
-   Strong domain boundary
-   Operational need
-   Team ownership requirement
-   Sustained performance bottleneck

The architecture must be horizontally scalable without requiring
microservices from day one.

------------------------------------------------------------------------

# 4. Security Rules

Security is a product requirement, not an optional enhancement.

## 4.1 Never Trust Client-Supplied Ownership

Never assume that a user is allowed to access an object simply because
the frontend sent its ID.

For example, this is unsafe:

``` text
GET /api/tasks/123
```

if the backend only checks whether task `123` exists.

The backend must verify that task `123` belongs to the authenticated
user or that the user otherwise has explicit permission to access it.

------------------------------------------------------------------------

## 4.2 Authorization Must Be Enforced Server-Side

The backend is the final authority for:

-   Authentication
-   Authorization
-   User ownership
-   Role checks
-   Data access

Frontend route protection is useful for UX but must never be considered
a security boundary.

------------------------------------------------------------------------

## 4.3 Password Security

Passwords must:

-   Never be stored in plaintext
-   Never be logged
-   Never be returned through APIs
-   Be stored using a strong password-hashing mechanism
-   Follow secure password-handling practices

Password hashes themselves must be treated as sensitive credentials.

------------------------------------------------------------------------

## 4.4 Secrets Must Never Be Committed

Never commit:

-   Database passwords
-   JWT secrets
-   API keys
-   Encryption keys
-   Cloud credentials
-   Third-party tokens
-   Production credentials

to Git.

Use environment variables or an appropriate secrets-management
mechanism.

------------------------------------------------------------------------

## 4.5 Authentication, Session & Token Security

Authentication must be handled securely with server-managed credentials:

-   Use secure server-managed authentication via HttpOnly cookies (Secure in production, appropriate SameSite policy).
-   CSRF protection must be implemented where applicable.
-   Authentication tokens must NEVER be stored in browser `localStorage`.
-   Never expose password hashes or authentication secrets through DTOs, responses, or logs.
-   Server-side authorization is mandatory on every protected endpoint; frontend route guards are for UX convenience only.
-   The backend must remain stateless and horizontally scalable.


------------------------------------------------------------------------

## 4.6 API Security

Every protected endpoint must enforce authentication and authorization.

APIs should also use:

-   Input validation
-   Rate limiting where appropriate
-   Safe error responses
-   HTTPS in production
-   Restricted CORS
-   Request-size limits where appropriate
-   Protection against common injection attacks
-   Pagination for large collections

------------------------------------------------------------------------

## 4.7 Do Not Leak Internal Information

Production API errors must not expose:

-   Stack traces
-   SQL queries
-   Database credentials
-   Internal file paths
-   Framework internals
-   Secrets
-   Other users' data

Users should receive useful but safe error messages.

Detailed technical information belongs in protected server logs.

------------------------------------------------------------------------

# 5. Privacy Rules

LifeOS handles highly personal productivity information. Privacy must
therefore be designed into the system.

## 5.1 User Data Is Private by Default

User data must be private unless the product explicitly introduces a
sharing capability.

Private data includes, but is not limited to:

-   Goals
-   Tasks
-   Habits
-   Notes
-   Learning plans
-   Calendar-related information
-   Personal progress
-   Analytics
-   Recommendations
-   Preferences

------------------------------------------------------------------------

## 5.2 Data Minimization

Only collect and store information required for the product.

Do not collect personal information merely because it might be useful
someday.

------------------------------------------------------------------------

## 5.3 Purpose Limitation

Data collected for one purpose must not automatically be reused for
unrelated purposes.

For example:

-   Task data may be used for task prioritization.
-   Habit data may be used for habit analytics.
-   Goal data may be used for goal-progress analysis.

Using personal data for a new purpose should be an intentional product
decision.

------------------------------------------------------------------------

## 5.4 No Private Data in Logs

Never log full:

-   Personal notes
-   Task descriptions unnecessarily
-   Habit details unnecessarily
-   Authentication credentials
-   Access tokens
-   Sensitive request bodies

Logs should contain operational information rather than users' private
content.

------------------------------------------------------------------------

## 5.5 Account Deletion

The system must support explicit user account deletion (`DELETE /api/v1/users/me`).

Account deletion rules:
- Must use controlled transactional deletion of all user-owned application data:
  - goals
  - milestones
  - tasks
  - habits
  - habit logs
  - learning items
  - learning sessions
  - events
  - notes
  - productivity data
- Do not rely on uncontrolled blind cascading behavior across the entire database.
- Preserve only records genuinely required for security, legal, or operational audit purposes.
- A deleted account must not leave unintentionally accessible personal data behind.

------------------------------------------------------------------------

## 5.6 Future AI Privacy Rule

If AI features are introduced:

-   Send only the minimum data required.
-   Never send the entire database by default.
-   Restrict AI access to the authenticated user's permitted data.
-   Do not expose another user's information through retrieval or
    prompts.
-   Clearly define what data is processed by external AI providers.
-   Do not treat an AI provider as an unrestricted database
    administrator.

------------------------------------------------------------------------

# 6. User Ownership & Entity Relationship Rules

Every user-owned entity must have a reliable ownership relationship.

Examples:

``` text
User
 ├── Goals
 ├── Tasks
 ├── Habits
 ├── Learning Items
 ├── Notes
 ├── Calendar Items
 └── Analytics / Preferences
```

## 6.1 Task, Milestone, and Goal Relationship Constraints

- `user_id` is REQUIRED on every Task.
- `goal_id` is OPTIONAL on Task (standalone tasks are valid).
- `milestone_id` is OPTIONAL on Task.
- Standalone tasks, goal-linked tasks, and milestone-linked tasks are all valid.
- If `milestone_id` is provided on a Task, the backend must strictly enforce:
  `milestone.user_id == task.user_id` AND `milestone.goal_id == task.goal_id`
- A task must NEVER reference a milestone belonging to another goal or another user.
- Server-side validation and ownership verification are mandatory.

## 6.2 Mandatory Ownership Rule

For every read, update, and delete operation:

``` text
Authenticated User
        ↓
Requested Resource
        ↓
Ownership / Permission Check
        ↓
Allow or Reject
```

ID manipulation must never allow:

``` text
User A → Resource owned by User B
```


------------------------------------------------------------------------

# 7. API Rules

## 7.1 Use Resource-Oriented REST APIs

Prefer clear resource naming.

Examples:

``` text
GET    /api/v1/goals
POST   /api/v1/goals
GET    /api/v1/goals/{id}
PUT    /api/v1/goals/{id}
DELETE /api/v1/goals/{id}
```

Avoid unnecessarily action-heavy endpoints unless an action genuinely
represents a state transition.

------------------------------------------------------------------------

## 7.2 Validate Every Input

Never assume frontend validation is enough.

Backend validation must handle:

-   Required fields
-   Length limits
-   Valid formats
-   Numeric ranges
-   Dates
-   Enum values
-   Relationships
-   Business constraints

------------------------------------------------------------------------

## 7.3 Use DTOs

Do not expose database entities directly as public API contracts when
doing so creates security, coupling, or maintenance problems.

Use request/response DTOs to control:

-   What clients can send
-   What clients can receive
-   API versioning
-   Sensitive-field exposure

------------------------------------------------------------------------

## 7.4 Consistent HTTP Semantics

Use appropriate HTTP methods and status codes.

Examples:

``` text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Unprocessable Entity
429 Too Many Requests
500 Internal Server Error
```

The exact status should match the API contract.

------------------------------------------------------------------------

## 7.5 Pagination

Collection endpoints must not return unlimited records.

Use pagination for potentially large datasets such as:

-   Tasks
-   Notes
-   Learning resources
-   Calendar events
-   Activity history

------------------------------------------------------------------------

# 8. Database Rules

## 8.1 MySQL Is the Source of Truth

For the initial architecture, MySQL is the primary persistent source of
truth.

Do not create unnecessary duplicate sources of persistent state.

------------------------------------------------------------------------

## 8.2 Maintain Referential Integrity

Use appropriate:

-   Primary keys
-   Foreign keys
-   Unique constraints
-   Not-null constraints
-   Check constraints where supported and useful

------------------------------------------------------------------------

## 8.3 Index Based on Real Queries

Indexes should support actual access patterns.

Likely high-value indexes may include combinations involving:

``` text
user_id
status
due_date
goal_id
created_at
```

Do not blindly index every column.

Indexes must be reviewed as the application grows.

------------------------------------------------------------------------

## 8.4 Avoid N+1 Query Problems

Data access should be designed to avoid unnecessary repeated queries.

Especially review:

-   Dashboard loading
-   Goal progress
-   Task lists
-   Analytics
-   Recommendation generation

------------------------------------------------------------------------

## 8.5 Use Transactions for Multi-Step State Changes

If an operation changes multiple related records and those changes must
remain consistent, use a transaction.

Example:

``` text
Complete Milestone
      ↓
Update milestone
      ↓
Update goal progress
      ↓
Record activity
```

If the operation must be atomic, all relevant changes should succeed or
fail together.

------------------------------------------------------------------------

# 9. Scalability Rules

LifeOS must be designed so that increasing traffic does not require a
complete rewrite.

## 9.1 Backend Should Be Stateless

A backend instance should not depend on local in-memory state for
critical user information.

This enables:

``` text
                ┌── Spring Boot Instance 1
Load Balancer ──┼── Spring Boot Instance 2
                └── Spring Boot Instance 3
```

Any instance should be able to process a valid request.

------------------------------------------------------------------------

## 9.2 Do Not Store Critical State Only in Local Memory

Avoid storing critical information such as:

-   User sessions
-   Persistent task state
-   Important recommendation state
-   Long-term analytics state

only in application memory.

Persistent state belongs in appropriate shared infrastructure.

------------------------------------------------------------------------

## 9.3 Optimize Before Scaling Infrastructure

Do not immediately add:

-   Redis
-   Kafka
-   Message brokers
-   Multiple databases
-   Microservices

without evidence.

First investigate:

1.  Database queries
2.  Application code
3.  API payload size
4.  Frontend rendering
5.  Caching opportunities
6.  Network latency

Then scale infrastructure where measurement justifies it.

------------------------------------------------------------------------

## 9.4 Caching Rules

Caching may be used for:

-   Repeated expensive reads
-   Reference data
-   Computed analytics
-   Recommendation results where appropriate

Cache keys must respect user isolation.

Never allow:

``` text
User A cache → User B response
```

User-specific cached information must contain a reliable user-specific
cache key.

------------------------------------------------------------------------

## 9.5 Heavy Work Should Be Asynchronous

Potentially expensive operations should be moved to background
processing when appropriate.

Examples:

-   Large analytics calculations
-   Report generation
-   Email/notification delivery
-   Future AI processing
-   Batch recommendation calculations

User-facing requests should not wait unnecessarily for expensive
background work.

------------------------------------------------------------------------

# 10. Recommendation Engine Rules

The LifeOS recommendation engine is a decision-support system.

## 10.1 Recommendations Must Be Explainable

A recommendation should ideally communicate why it was generated.

Example:

``` text
Recommendation:
Focus on "DSA Practice"

Reason:
Your interview goal is approaching,
the task has high priority,
and your recent DSA activity is below target.
```

------------------------------------------------------------------------

## 10.2 Recommendations Must Be Data-Based

Recommendations should use defined signals such as:

-   Goal priority
-   Deadline proximity
-   Task priority
-   Task status
-   Habit consistency
-   Learning progress
-   Available time
-   Recent activity

Avoid arbitrary recommendations.

------------------------------------------------------------------------

## 10.3 Recommendations Are Suggestions

A recommendation must not automatically become user state unless the
user explicitly chooses to apply it.

------------------------------------------------------------------------

## 10.4 Recommendation Logic Must Be Testable

Rules should be isolated enough to test independently.

For example:

``` text
Input:
Goal priority = HIGH
Deadline = Near
Task status = PENDING
Recent activity = LOW

Output:
Priority score increases
```

The scoring/rule behavior should be deterministic where possible.

------------------------------------------------------------------------

## 10.5 Recommendations Are Dynamic and Stateless for MVP

- For MVP, recommendations and Daily Focus are dynamic and stateless, calculated on demand from current real data by `RecommendationService`.
- Do not create a persistent `recommendations` database table for MVP.
- Do not introduce Redis solely for recommendations.
- Scoring constants must be centralized and configurable, not scattered as magic numbers throughout the codebase.
- The architecture remains extensible to future recommendation persistence or caching if real scale demands it.


------------------------------------------------------------------------

# 11. Analytics Rules

Analytics must help the user understand their system rather than
overwhelm them with charts.

Useful analytics should answer questions such as:

-   Am I progressing toward my goals?
-   Which areas are receiving attention?
-   Which habits are consistent?
-   Which goals are at risk?
-   Where am I spending time?
-   What should I focus on next?

Avoid charts that exist only because charts look impressive.

------------------------------------------------------------------------

# 12. Goal Health Rules

Goal health should consider meaningful signals such as:

``` text
Progress
Deadline
Milestone completion
Task completion
Recent activity
Consistency
```

A goal should not be labelled "healthy" or "at risk" based on a single
arbitrary number.

Health calculations should be explainable.

------------------------------------------------------------------------

# 13. Frontend & UX Rules

## 13.1 Mobile Is a First-Class Platform

LifeOS must provide a strong experience on:

-   Mobile phones
-   Tablets / iPads
-   Laptops
-   Desktop screens

Mobile responsiveness is not a final polishing task.

It must influence component and layout decisions from the beginning.

------------------------------------------------------------------------

## 13.2 No Desktop UI Shrunk Onto Mobile

Do not simply reduce desktop dimensions.

Mobile may require:

-   Collapsed navigation
-   Stacked cards
-   Simplified tables
-   Bottom navigation
-   Scrollable sections
-   Full-width forms
-   Mobile-friendly modals
-   Touch-friendly controls

------------------------------------------------------------------------

## 13.3 No Unnecessary Horizontal Scrolling

Users should not have to horizontally scroll the entire page to use core
functionality.

If a table cannot reasonably fit on mobile, redesign the presentation
rather than forcing the desktop table into a tiny viewport.

------------------------------------------------------------------------

## 13.4 Touch-Friendly Controls

Interactive elements must be comfortably usable on touch devices.

Avoid:

-   Tiny buttons
-   Closely packed controls
-   Hover-only functionality
-   Desktop-only context menus
-   Interactions that require precise mouse positioning

------------------------------------------------------------------------

## 13.5 Responsive Charts

Charts and analytics must remain readable on smaller screens.

Do not allow:

-   Cropped charts
-   Unreadable labels
-   Overlapping legends
-   Excessively dense data

------------------------------------------------------------------------

## 13.6 Responsive Forms and Modals

Forms and dialogs must adapt to screen size.

On mobile:

-   Inputs should fit the viewport.
-   Important actions should remain visible.
-   Keyboard interaction should not break the layout.
-   Long forms should be easy to navigate.

------------------------------------------------------------------------

## 13.7 Every Important Screen Needs Four States

Where applicable, frontend screens should handle:

``` text
Loading
Success / Data
Empty
Error
```

Do not build only the happy path.

------------------------------------------------------------------------

## 13.8 Accessibility

Consider:

-   Keyboard navigation
-   Visible focus states
-   Semantic HTML
-   Form labels
-   Sufficient contrast
-   Meaningful error messages
-   Screen-reader-friendly controls where applicable

Accessibility should be part of implementation rather than an
afterthought.

------------------------------------------------------------------------

# 14. Coding & Maintainability Rules

## 14.1 Prefer Readable Code

Code should be understandable by another developer without requiring the
original author to explain every line.

------------------------------------------------------------------------

## 14.2 Avoid Giant Components

Do not create:

-   1,000-line React components
-   Giant Spring controllers
-   God services
-   Huge utility files containing unrelated logic

Split code around meaningful responsibilities.

------------------------------------------------------------------------

## 14.3 Avoid Unnecessary Duplication

If the same business rule exists in multiple places, centralize it where
appropriate.

Example:

Do not calculate goal health differently in:

``` text
Dashboard
Goal page
Analytics page
Recommendation engine
```

unless the differences are intentional and documented.

------------------------------------------------------------------------

## 14.4 Keep Dependencies Justified

Do not add libraries only because they are popular.

Before adding a dependency, consider:

-   Why is it needed?
-   Can the existing stack solve the problem?
-   Does it introduce security risk?
-   Does it increase bundle size?
-   Is it actively maintained?
-   Does it complicate deployment?

------------------------------------------------------------------------

## 14.5 Meaningful Naming

Names should communicate intent.

Prefer:

``` text
calculateGoalHealth()
getPendingTasks()
generateDailyRecommendations()
```

over vague names such as:

``` text
process()
handle()
doStuff()
```

when more specific naming is possible.

------------------------------------------------------------------------

# 15. Error Handling Rules

Errors must be predictable and useful.

## Backend

Use a centralized error-handling approach where appropriate.

Return consistent error structures such as:

``` json
{
  "status": 400,
  "message": "Invalid task data",
  "code": "TASK_VALIDATION_ERROR"
}
```

Do not expose internal implementation details.

## Frontend

The UI should:

-   Show useful messages
-   Avoid crashing on API failures
-   Provide retry options where appropriate
-   Preserve user input when possible
-   Distinguish empty data from failed requests

------------------------------------------------------------------------

# 16. Observability Rules

Production systems must be observable.

Monitor:

-   Request latency
-   Error rates
-   Database performance
-   Resource utilization
-   Authentication failures
-   Important background jobs
-   Application health

Logs should be:

-   Structured where practical
-   Searchable
-   Useful for debugging
-   Free of sensitive user content

------------------------------------------------------------------------

# 17. Reliability Rules

LifeOS should degrade gracefully.

Examples:

### If recommendation generation fails

The core application should still allow users to:

-   View tasks
-   Create tasks
-   Complete tasks
-   View goals
-   Use other core functionality

### If analytics calculation fails

Core CRUD functionality should not become unusable.

### If an optional third-party service fails

The entire application should not unnecessarily fail.

Critical user data should not depend on optional external services.

------------------------------------------------------------------------

# 18. Development Workflow Rules

Every feature implementation should follow this sequence:

``` text
Understand requirement
        ↓
Check PRD
        ↓
Check architecture
        ↓
Check rules
        ↓
Design data/API changes
        ↓
Implement backend
        ↓
Implement frontend
        ↓
Test security & ownership
        ↓
Test responsive UX
        ↓
Test failure states
        ↓
Review performance
        ↓
Update documentation if needed
```

------------------------------------------------------------------------

## 18.1 Before Changing Existing Code

First understand:

-   Existing architecture
-   Related modules
-   Existing API contracts
-   Database relationships
-   Authentication/authorization flow
-   Existing UI patterns

Do not rewrite working architecture simply because another approach
looks cleaner.

------------------------------------------------------------------------

## 18.2 Avoid Unrelated Changes

A feature change should not casually modify unrelated modules.

Keep pull requests and commits focused.

------------------------------------------------------------------------

## 18.3 API Changes Must Be Intentional

Do not silently break existing frontend-backend contracts.

If an API changes:

-   Update the frontend
-   Update documentation
-   Update tests
-   Consider backward compatibility where appropriate

------------------------------------------------------------------------

# 19. Feature Completion Checklist

A feature is not considered complete simply because the main screen
works.

For every backend feature, verify:

-   Authentication
-   Authorization
-   User ownership
-   Input validation
-   Error handling
-   Database consistency
-   Performance
-   Appropriate indexes
-   Tests

For every frontend feature, verify:

-   Loading state
-   Success state
-   Empty state
-   Error state
-   Mobile layout
-   Tablet layout
-   Desktop layout
-   Touch interaction
-   Accessibility
-   API failure handling

------------------------------------------------------------------------

# 20. Testing Rules

Testing priority should focus on business-critical behavior.

High-priority tests include:

### Authentication

-   Valid login
-   Invalid login
-   Expired/invalid token
-   Unauthorized access

### Ownership

-   User can access own data
-   User cannot access another user's data
-   ID manipulation is rejected

### Core workflows

-   Create goal
-   Update goal
-   Create task
-   Complete task
-   Create habit
-   Record habit completion
-   Create learning item
-   Create note
-   Generate recommendation

### Recommendation Engine

Test important scoring and rule combinations.

### Responsive UX

Test critical flows on:

-   Mobile
-   Tablet
-   Desktop

------------------------------------------------------------------------

# 21. Git & Repository Rules

Use clear commit messages.

Examples:

``` text
feat: add goal creation API
feat: add mobile dashboard navigation
fix: prevent cross-user task access
fix: handle empty task state
refactor: extract goal health service
test: add recommendation engine tests
docs: update architecture
```

Do not commit:

-   Secrets
-   Temporary files
-   Build artifacts
-   Personal data
-   Debug logs
-   Unnecessary generated files

------------------------------------------------------------------------

# 22. Documentation Rules

Important architectural decisions must be documented.

The repository should maintain:

``` text
docs/
├── prd.md
├── architecture.md
├── rules.md
├── api.md
├── database.md
└── decisions/
    ├── ADR-001-modular-monolith.md
    ├── ADR-002-authentication.md
    └── ADR-003-recommendation-engine.md
```

Documentation must evolve with meaningful architectural changes.

------------------------------------------------------------------------

# 23. AI-Agent / Copilot Rules

If AI coding assistants are used to build LifeOS, generated code must
still follow this document.

AI-generated code must not be accepted blindly.

Before accepting AI-generated implementation, verify:

1.  Does it follow the architecture?
2.  Does it enforce authorization?
3.  Does it verify ownership?
4.  Does it expose sensitive information?
5.  Does it introduce unnecessary dependencies?
6.  Does it work with the database model?
7.  Does it handle errors?
8.  Does it work on mobile?
9.  Does it introduce scalability problems?
10. Does it break an existing API contract?

AI is an implementation assistant, not the final authority.

------------------------------------------------------------------------

# 24. Production Readiness Rules

Before production release, verify:

### Security

-   HTTPS enabled
-   Secrets externalized
-   Authentication secured
-   Authorization tested
-   Ownership checks tested
-   Rate limiting considered
-   CORS restricted
-   Error leakage prevented

### Privacy

-   Data collection minimized
-   Private data protected
-   Sensitive logs removed
-   Account deletion considered
-   Third-party data sharing reviewed

### Performance

-   Database queries reviewed
-   Indexes reviewed
-   Pagination implemented
-   Frontend bundle reviewed
-   API latency measured
-   Heavy tasks moved to background processing where appropriate

### Reliability

-   Health checks available
-   Monitoring available
-   Error tracking available
-   Backup/recovery strategy defined
-   Failure behavior tested

### UX

-   Mobile tested
-   Tablet tested
-   Desktop tested
-   Loading states tested
-   Empty states tested
-   Error states tested
-   Accessibility reviewed

------------------------------------------------------------------------

# 25. Rules for Future Scaling

The scaling path should remain incremental:

``` text
Stage 1
Single application
+
MySQL
+
Basic monitoring

        ↓

Stage 2
Multiple stateless Spring Boot instances
+
Load Balancer
+
Database optimization

        ↓

Stage 3
Redis where measurement justifies it
+
Background workers
+
Improved observability

        ↓

Stage 4
Read replicas / specialized infrastructure
+
More advanced asynchronous processing

        ↓

Stage 5
Extract individual services only when
real scale or domain requirements justify it
```

The system should scale because the architecture requires it, not
because the architecture diagram looks impressive.

------------------------------------------------------------------------

# 26. Final Engineering Principles

These principles summarize the entire document.

### Rule 1 --- User value over feature count

Build what improves the user's life.

### Rule 2 --- Security is mandatory

Every user-owned operation must be protected.

### Rule 3 --- Privacy by default

Personal productivity data belongs to the user.

### Rule 4 --- Backend is the security boundary

Never rely on frontend checks for authorization.

### Rule 5 --- Modular before distributed

Start simple, but design cleanly enough to scale.

### Rule 6 --- Measure before optimizing infrastructure

Do not add complexity without evidence.

### Rule 7 --- Mobile is first-class

A feature that works only on desktop is incomplete.

### Rule 8 --- Recommendations must be explainable

Users should understand why LifeOS suggests something.

### Rule 9 --- Core functionality must survive optional failures

Analytics, recommendations, or future AI must not unnecessarily break
basic productivity workflows.

### Rule 10 --- Documentation is part of engineering

Important decisions must be recorded.

### Rule 11 --- AI-generated code still follows human-defined rules

No generated code bypasses security, privacy, architecture, or quality
requirements.

### Rule 12 --- Build for real users

LifeOS should be engineered as a product that can grow beyond the
classroom.

------------------------------------------------------------------------

# 27. Rule of Last Resort

When two reasonable implementation choices exist, prefer the one that
is:

**simpler + safer + more maintainable + more privacy-preserving + easier
to scale + better for the user.**

Do not choose complexity merely because it appears more sophisticated.

------------------------------------------------------------------------

**End of `rules.md`**
