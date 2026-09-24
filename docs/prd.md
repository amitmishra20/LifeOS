# LifeOS - Product Requirements Document

**Version:** 1.0\
**Status:** MVP Definition\
**Project Type:** Academic Mini Project / Portfolio Product\
**Primary Stack:** React.js, Spring Boot, MySQL

------------------------------------------------------------------------

## 1. Product Overview

### 1.1 Product Name

**LifeOS**

### 1.2 One-Line Description

LifeOS is a web-based personal operating system that connects goals,
milestones, tasks, habits, learning activities, calendar events, and
productivity analytics into one integrated workflow.

### 1.3 Product Vision

Most productivity applications manage individual pieces of a person's
life. LifeOS aims to connect those pieces so that users can understand
not only **what they need to do**, but also **why they are doing it, how
consistently they are doing it, whether they are progressing toward
their goals, and what they should prioritize next**.

The product follows this continuous loop:

> **Set Goals → Break Down → Execute → Track → Analyze → Recommend →
> Improve**

### 1.4 Problem Statement

Students and young professionals commonly use separate tools for:

-   To-do lists
-   Goal tracking
-   Habit tracking
-   Learning progress
-   Calendar scheduling
-   Notes
-   Productivity statistics

Because these systems are disconnected, users often lose the
relationship between their long-term goals and daily actions.

For example, a user may have a goal of getting a software development
job but manage DSA preparation, project development, applications,
habits, and deadlines in separate places.

LifeOS solves this by creating a connected system where:

**Goals → Milestones → Tasks → Completion Data → Analytics →
Recommendations**

------------------------------------------------------------------------

# 2. Product Goals

## 2.1 Primary Goals

LifeOS should:

1.  Allow users to create and manage long-term goals.
2.  Break goals into milestones and actionable tasks.
3.  Allow users to manage daily tasks.
4.  Track recurring habits and consistency.
5.  Track learning activities and progress.
6.  Manage important dates and calendar events.
7.  Provide a centralized dashboard.
8.  Analyze productivity and goal progress.
9.  Detect goals that are falling behind.
10. Provide rule-based recommendations and daily priorities.

## 2.2 Secondary Goals

-   Provide a clean and modern user experience.
-   Demonstrate full-stack development skills.
-   Demonstrate REST API architecture.
-   Demonstrate relational database design.
-   Demonstrate business logic and analytics.
-   Provide a strong academic project with a clear technical
    architecture.

## 2.3 Non-Goals for MVP

The first version will NOT attempt to build:

-   A full AI chatbot.
-   A separate native mobile application (the responsive web application is in MVP scope).
-   A social network.
-   A financial management system.
-   Wearable-device integration.
-   Complex machine-learning prediction models.
-   Full Google Calendar synchronization.
-   Voice-controlled productivity management.

These can be considered future extensions.

------------------------------------------------------------------------

# 3. Target Users

## 3.1 Primary User

Students and young professionals who want to organize:

-   Academic goals
-   Career preparation
-   Personal projects
-   Learning
-   Daily tasks
-   Habits
-   Deadlines

## 3.2 Example User

A computer science student preparing for software placements.

The student may have:

**Goal:** Get a software developer job.

**Milestones:**

-   Improve DSA
-   Build projects
-   Improve Java
-   Prepare resume
-   Apply for jobs
-   Prepare for interviews

**Tasks:**

-   Solve 2 array problems
-   Complete Spring Boot API
-   Revise SQL joins
-   Apply to 3 companies

**Habits:**

-   Code for 90 minutes
-   Exercise
-   Read technical material

LifeOS connects these activities to the larger placement goal.

------------------------------------------------------------------------

# 4. Core Product Workflow

## 4.1 High-Level Workflow

``` text
User
  |
  v
Create Goal
  |
  v
Create Milestones
  |
  v
Create Tasks
  |
  v
Execute Daily Activities
  |
  +----> Complete Tasks
  |
  +----> Log Habits
  |
  +----> Update Learning Progress
  |
  +----> Record Events
  |
  v
LifeOS Collects Activity Data
  |
  v
Analytics Engine
  |
  +----> Goal Progress
  +----> Task Completion
  +----> Habit Consistency
  +----> Learning Progress
  +----> Productivity Score
  |
  v
Recommendation Engine
  |
  +----> Daily Focus
  +----> Priority Suggestions
  +----> Risk Alerts
  +----> Rescheduling Suggestions
  |
  v
User Improves Plan
  |
  +--------------------------+
                             |
                             v
                         Continuous Loop
```

------------------------------------------------------------------------

# 5. Core User Journey

## Step 1: User Registration

The user creates an account.

The system stores:

-   Name
-   Email
-   Password
-   Account creation date
-   User preferences

## Step 2: User Creates a Goal

Example:

> Get a software developer placement.

The goal contains:

-   Title
-   Description
-   Category
-   Start date
-   Target date
-   Priority
-   Status
-   Progress

## Step 3: User Creates Milestones

Example:

``` text
Goal: Software Developer Placement

Milestone 1: DSA Preparation
Milestone 2: Development Skills
Milestone 3: Projects
Milestone 4: Resume
Milestone 5: Interview Preparation
```

## Step 4: User Creates Tasks

Example:

``` text
Milestone: DSA Preparation

Task 1: Complete Arrays
Task 2: Complete Strings
Task 3: Practice Hashing
Task 4: Solve 10 Problems
```

## Step 5: User Executes Tasks

The user marks tasks:

-   Pending
-   In Progress
-   Completed
-   Overdue

## Step 6: User Tracks Habits

Example:

``` text
Habit: Coding Practice

Monday    ✓
Tuesday   ✓
Wednesday ✓
Thursday  ✓
Friday    ✗
Saturday  ✓
Sunday    ✓
```

The system calculates:

-   Completion rate
-   Current streak
-   Longest streak
-   Weekly consistency

## Step 7: LifeOS Analyzes Progress

The system calculates:

-   Goal progress
-   Task completion
-   Habit consistency
-   Learning progress
-   Productivity score

## Step 8: LifeOS Generates Insights

Example:

> Your DSA goal is behind its expected progress.

or:

> You have three high-priority tasks due within two days.

## Step 9: User Adjusts Their Plan

The user can:

-   Complete tasks
-   Reschedule tasks
-   Change priorities
-   Update milestones
-   Adjust habits

The system then recalculates progress.

------------------------------------------------------------------------

# 6. Functional Requirements

# 6.1 Authentication

The system shall allow users to:

-   Register.
-   Log in.
-   Log out.
-   Maintain an authenticated session.
-   Access only their own data.

### Required Fields

``` text
User
- id
- name
- email
- password
- createdAt
- updatedAt
```

### Security & Authentication

- Passwords must never be stored as plain text and must be hashed using a strong algorithm (BCrypt/Argon2).
- Never expose password hashes or authentication secrets through DTOs or logs.
- Use secure server-managed authentication via HttpOnly cookies (Secure in production, appropriate SameSite policy).
- Provide CSRF protection where applicable.
- Authentication tokens must never be stored in browser `localStorage`.
- Server-side authorization remains mandatory on every private endpoint; frontend route guards are for UX only.
- The backend remains stateless and horizontally scalable.


------------------------------------------------------------------------

# 6.2 Dashboard

The dashboard is the primary screen after login.

It should display:

### Summary

-   Active goals
-   Today's tasks
-   Habit completion
-   Learning progress
-   Productivity score

### Today's Focus

The dashboard should show the most important tasks for the current day.

Example:

``` text
Today's Focus

□ Solve 2 DSA problems
□ Complete Spring Boot API
✓ Revise SQL
□ Work on LifeOS frontend
```

### Goal Health

Each active goal should show:

-   Progress percentage
-   Target date
-   Status
-   Health indicator

Possible states:

``` text
On Track
At Risk
Behind
Completed
```

### Habit Summary

Show:

-   Today's completion
-   Current streak
-   Weekly consistency

### LifeOS Insight

Show the most important recommendation generated by the recommendation
engine.

------------------------------------------------------------------------

# 6.3 Goals

Users shall be able to:

-   Create goals.
-   Edit goals.
-   Delete/archive goals.
-   View goals.
-   Change goal status.
-   Set target dates.
-   Set priority.
-   View progress.

### Goal Fields

``` text
Goal
- id
- userId
- title
- description
- category
- priority
- startDate
- targetDate
- status
- progress
- createdAt
- updatedAt
```

### Goal Status

``` text
ACTIVE
COMPLETED
PAUSED
ARCHIVED
```

------------------------------------------------------------------------

# 6.4 Milestones

Milestones break a large goal into meaningful phases.

Users shall be able to:

-   Create milestones.
-   Edit milestones.
-   Delete milestones.
-   Mark milestones complete.
-   Assign milestones to goals.

### Milestone Fields

``` text
Milestone
- id
- goalId
- title
- description
- targetDate
- status
- progress
```

------------------------------------------------------------------------

# 6.5 Tasks

Users shall be able to:

-   Create tasks.
-   Edit tasks.
-   Delete tasks.
-   Complete tasks.
-   Assign priorities.
-   Set deadlines.
-   Assign tasks to goals/milestones.
-   Filter tasks.
-   Sort tasks.

### Task Status

``` text
TODO
IN_PROGRESS
COMPLETED
OVERDUE
```

### Task Priority

``` text
LOW
MEDIUM
HIGH
CRITICAL
```

### Task Fields

``` text
Task
- id
- userId (REQUIRED)
- goalId (OPTIONAL)
- milestoneId (OPTIONAL)
- title
- description
- priority
- status
- dueDate
- estimatedMinutes
- completedAt
- createdAt
- updatedAt
```

### Task Relationship Rules

- Standalone tasks are valid (`goalId` = null, `milestoneId` = null).
- Goal-linked tasks are valid (`goalId` set, `milestoneId` = null).
- Milestone-linked tasks are valid (`goalId` set, `milestoneId` set).
- If `milestoneId` is provided, the backend enforces:
  `milestone.userId == task.userId` AND `milestone.goalId == task.goalId`.
- A task must never reference a milestone belonging to another goal or another user.
- Server-side validation and ownership checks are mandatory.


------------------------------------------------------------------------

# 6.6 Habits

Users shall be able to:

-   Create habits.
-   Edit habits.
-   Archive habits.
-   Log daily completion.
-   View streaks.
-   View weekly consistency.

### Example Habits

-   Coding
-   Exercise
-   Reading
-   Meditation
-   Study

### Habit Fields

``` text
Habit
- id
- userId
- name
- description
- frequency
- targetPerWeek
- status
- createdAt
```

### Habit Log

``` text
HabitLog
- id
- habitId
- date
- completed
- notes
```

### Calculated Metrics

-   Current streak
-   Longest streak
-   Weekly completion rate
-   Monthly completion rate

------------------------------------------------------------------------

# 6.7 Learning Tracker

The learning module allows users to track skills or subjects.

Example:

``` text
Java       70%
DSA        55%
SQL        80%
Spring     40%
React      60%
```

Users shall be able to:

-   Create learning subjects.
-   Update progress.
-   Set learning goals.
-   Record learning sessions.
-   View progress history.

### Learning Fields

``` text
LearningItem
- id
- userId
- name
- category
- targetProgress
- currentProgress
- status
```

### Learning Session

``` text
LearningSession
- id
- learningItemId
- date
- durationMinutes
- topic
- notes
```

------------------------------------------------------------------------

# 6.8 Calendar

The calendar provides a centralized view of:

-   Task deadlines
-   Goal deadlines
-   Learning sessions
-   Custom events

Users shall be able to:

-   Create events.
-   Edit events.
-   Delete events.
-   View events by day/week/month.

### Event Fields

``` text
Event
- id
- userId
- title
- description
- startTime
- endTime
- type
```

------------------------------------------------------------------------

# 6.9 Notes

The notes module provides lightweight knowledge management.

Users shall be able to:

-   Create notes.
-   Edit notes.
-   Delete notes.
-   Search notes.
-   Categorize notes.

### Note Fields

``` text
Note
- id
- userId
- title
- content
- category
- createdAt
- updatedAt
```

------------------------------------------------------------------------

# 6.10 Analytics

Analytics should transform raw activity data into useful metrics.

### Required Metrics

#### Goal Progress

``` text
Completed milestone progress / Total milestone progress
```

#### Task Completion

``` text
Completed tasks / Total planned tasks
```

#### Habit Consistency

``` text
Completed habit logs / Expected habit logs
```

#### Learning Progress

Based on user-entered learning progress and learning sessions.

#### Productivity Score

The MVP can calculate a weighted score from:

``` text
Task Completion       30%
Habit Consistency     25%
Goal Progress         25%
Learning Activity     20%
```

The weights should be configurable in the backend rather than hard-coded
throughout the application.

------------------------------------------------------------------------

# 7. Recommendation Engine

The recommendation engine is one of the most important differentiating
features of LifeOS.

The MVP uses deterministic, explainable business rules rather than machine
learning. Recommendations are dynamic and stateless in MVP (no dedicated
database table or Redis cache required).

## 7.1 Goal Status vs. Goal Health

Goal lifecycle status and goal health are distinct concepts:

### Goal Lifecycle Status
- `ACTIVE`
- `PAUSED`
- `COMPLETED`
- `ARCHIVED`

### Goal Health (Derived Evaluation)
- `ON_TRACK`
- `AT_RISK`
- `BEHIND`
- `COMPLETED`

`AT_RISK` and `BEHIND` are health states, never lifecycle status values.

### Goal Health Calculation Rules

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

These thresholds are configurable business rules in the backend rather than
hardcoded values.

## 7.2 Deadline Risk

``` text
IF task_due_date <= 2 days
AND task_status != COMPLETED
THEN priority = HIGH
```

## 7.3 Habit Consistency Warning

``` text
IF weekly_completion_rate < 50%
THEN generate habit recommendation
```

## 7.4 Task Overload

``` text
IF high_priority_task_count is high
AND available_time is insufficient
THEN recommend rescheduling lower-priority tasks
```

## 7.5 Daily Focus

The recommendation engine scores and ranks incomplete eligible tasks to determine
the user's Daily Focus.

------------------------------------------------------------------------

# 8. Daily Focus Algorithm

The MVP uses a deterministic, explainable scoring model:

``` text
Task Score = Priority Score + Deadline Score + Goal Health Score + Overdue Score
```

### Centralized Scoring Constants

``` text
Priority Score:
  LOW       = 10
  MEDIUM    = 20
  HIGH      = 30
  CRITICAL  = 40

Deadline Score:
  Due today      = +40
  Due tomorrow   = +30
  Due in 2 days  = +20
  Due in 3-7 days= +10
  Due > 7 days   = +0

Overdue Score:
  Task overdue   = +50

Goal Health Score:
  Linked goal BEHIND  = +30
  Linked goal AT_RISK = +15
  Linked goal ON_TRACK= +0
  No linked goal      = +0
```

The system evaluates all eligible incomplete tasks (`status IN ('TODO', 'IN_PROGRESS', 'OVERDUE')`),
sorts by `Task Score` descending, and returns the top 5 tasks as the user's Daily Focus.
The scoring constants are centralized and configurable, and each recommended task includes
a human-readable explanation of why it was prioritized.


------------------------------------------------------------------------

# 9. UI/UX Requirements

## 9.1 Design Direction

LifeOS should have a modern productivity-dashboard aesthetic.

Design characteristics:

-   Clean
-   Minimal
-   Professional
-   Card-based
-   Spacious
-   Responsive
-   Data-focused
-   Subtle animations
-   Strong visual hierarchy

The interface should avoid looking like a generic college CRUD
application.

## 9.2 Primary Navigation

The left sidebar uses grouped intent-based navigation sections:

``` text
LifeOS

HOME
  Dashboard

FOCUS
  Today's Focus
  Tasks
  Habits

PLAN
  Goals
  Calendar

GROW
  Learning
  Progress

REFLECT
  Analytics
  Goal Health
  Recommendations

CAPTURE
  Notes

----------------
Settings
Logout
```

## 9.3 Dashboard Layout

The dashboard should contain:

1.  Greeting/header
2.  Summary cards
3.  Today's Focus
4.  Goal Health
5.  Habit Tracker
6.  Calendar preview
7.  Analytics preview
8.  LifeOS Insight

## 9.4 Visual Language

Design system language:

-   Graphite / Midnight foundation (neutral dark/light surfaces)
-   Deep Indigo primary accent
-   Restrained semantic indicators:
    -   Emerald for healthy / completed states
    -   Amber for warnings / at-risk states
    -   Crimson for behind / critical / overdue states
-   Consistent design tokens (colors, typography, spacing, radius, shadows, motion)
-   No excessive gradients, neon, glow, glassmorphism, or decorative visual effects
-   Clear typography and calm visual hierarchy prioritizing readability over decoration


------------------------------------------------------------------------

# 10. Frontend Requirements

## Technology

**React.js**

Recommended supporting technologies:

-   React Router
-   Axios
-   Tailwind CSS
-   Chart library such as Recharts
-   Form validation library where useful

## Frontend Architecture

``` text
src/
│
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

### Pages

``` text
Login
Register
Dashboard
Goals
Goal Details
Tasks
Habits
Learning
Calendar
Notes
Analytics
Settings
```

------------------------------------------------------------------------

# 11. Backend Requirements

## Technology

**Spring Boot**

Recommended components:

-   Spring Web
-   Spring Data JPA
-   Spring Security
-   Bean Validation
-   MySQL Driver

## Backend Architecture

``` text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

Additional layer:

``` text
Analytics Service
Recommendation Service
```

## Suggested Package Structure

``` text
com.lifeos
│
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── exception
├── analytics
├── recommendation
└── config
```

------------------------------------------------------------------------

# 12. REST API Requirements

All application APIs are standardized under the `/api/v1/` prefix:

## Authentication & Users

``` text
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/logout
GET    /api/v1/auth/me
DELETE /api/v1/users/me
```

## Goals

``` text
GET    /api/v1/goals
GET    /api/v1/goals/{id}
POST   /api/v1/goals
PUT    /api/v1/goals/{id}
DELETE /api/v1/goals/{id}
```

## Milestones

``` text
GET    /api/v1/goals/{goalId}/milestones
POST   /api/v1/goals/{goalId}/milestones
GET    /api/v1/milestones/{id}
PUT    /api/v1/milestones/{id}
DELETE /api/v1/milestones/{id}
```

## Tasks

``` text
GET    /api/v1/tasks
GET    /api/v1/tasks/{id}
POST   /api/v1/tasks
PUT    /api/v1/tasks/{id}
DELETE /api/v1/tasks/{id}
PATCH  /api/v1/tasks/{id}/complete
```

## Habits

``` text
GET    /api/v1/habits
GET    /api/v1/habits/{id}
POST   /api/v1/habits
PUT    /api/v1/habits/{id}
DELETE /api/v1/habits/{id}
POST   /api/v1/habits/{id}/logs
```

## Learning

``` text
GET    /api/v1/learning
GET    /api/v1/learning/{id}
POST   /api/v1/learning
PUT    /api/v1/learning/{id}
DELETE /api/v1/learning/{id}
POST   /api/v1/learning/{id}/sessions
```

## Calendar Events

``` text
GET    /api/v1/events
GET    /api/v1/events/{id}
POST   /api/v1/events
PUT    /api/v1/events/{id}
DELETE /api/v1/events/{id}
```

## Notes

``` text
GET    /api/v1/notes
GET    /api/v1/notes/{id}
POST   /api/v1/notes
PUT    /api/v1/notes/{id}
DELETE /api/v1/notes/{id}
```

## Analytics

``` text
GET /api/v1/analytics/dashboard
GET /api/v1/analytics/productivity
GET /api/v1/analytics/goals
GET /api/v1/analytics/habits
```

## Recommendations

``` text
GET /api/v1/recommendations
GET /api/v1/recommendations/daily-focus
```

------------------------------------------------------------------------

# 13. Database Design

Core tables:

``` text
users
goals
milestones
tasks
habits
habit_logs
learning_items
learning_sessions
events
notes
productivity_logs
```

> **Note on Recommendations:** For MVP, recommendations are dynamic and stateless,
> computed on demand by `RecommendationService`. No persistent `recommendations`
> database table is created.


## Core Relationships

``` text
User
 |
 +---- Goal
 |       |
 |       +---- Milestone
 |               |
 |               +---- Task
 |
 +---- Task
 |
 +---- Habit
 |       |
 |       +---- HabitLog
 |
 +---- LearningItem
 |       |
 |       +---- LearningSession
 |
 +---- Event
 |
 +---- Note
```

All user-owned records must be associated with a user and protected from
access by other users.

------------------------------------------------------------------------

# 14. Business Rules

## Goal Progress

Goal progress should be derived primarily from milestone/task completion
rather than allowing arbitrary conflicting values everywhere.

## Goal Health

A goal can be classified as:

``` text
ON_TRACK
AT_RISK
BEHIND
COMPLETED
```

based on progress versus expected progress and remaining time.

## Task Status

A task whose deadline has passed while incomplete should be considered
overdue.

## Habit Streak

A streak increments when the required habit occurrence is completed
consecutively according to the habit frequency.

## Recommendation Expiration

Recommendations should not remain permanently active once their
triggering condition disappears.

------------------------------------------------------------------------

# 15. Error Handling

The application should provide consistent API error responses.

Example:

``` json
{
  "timestamp": "2026-08-19T12:00:00",
  "status": 404,
  "message": "Goal not found",
  "path": "/api/goals/10"
}
```

Frontend should display human-readable messages rather than raw backend
exceptions.

------------------------------------------------------------------------

# 16. Security & Privacy Requirements

The application should:

-   Hash passwords using strong algorithms (BCrypt/Argon2).
-   Use secure server-managed authentication via HttpOnly cookies (never `localStorage`).
-   Validate user input server-side.
-   Protect private APIs and enforce server-side authorization.
-   Prevent unauthorized access to another user's records.
-   Validate ownership before accessing, modifying, or deleting resources.
-   Avoid exposing sensitive information in API responses or logs.
-   Use environment variables for secrets and database credentials.
-   Support explicit user account deletion via `DELETE /api/v1/users/me` with
    controlled transactional deletion of all user-owned application data
    (goals, milestones, tasks, habits, habit logs, learning items, learning sessions,
    events, notes, productivity logs), preserving only records genuinely required
    for security or operational auditing.


------------------------------------------------------------------------

# 17. Performance Requirements

For MVP:

-   Dashboard should load within a reasonable response time under normal
    development/demo usage.
-   Database queries should avoid unnecessary repeated requests.
-   Pagination should be used where datasets can grow significantly.
-   API responses should return only necessary data.
-   Analytics calculations should be optimized as the dataset grows.

------------------------------------------------------------------------

# 18. Responsive Design

LifeOS must provide a polished, responsive, and consistent user experience
across all major device categories:

-   Desktop
-   Laptop
-   Tablet / iPad
-   Mobile phones

Although desktop and laptop provide the primary development and demonstration
environment, **mobile responsiveness is a first-class product requirement,
not a secondary enhancement**. The application must not simply shrink the
desktop interface on smaller screens.

## 18.1 Responsive Design Principles

The UI should adapt its layout, navigation, content density, controls, and
interaction patterns according to the available screen size.

The responsive experience should prioritize:

-   Mobile usability
-   Touch-friendly interactions
-   Readability
-   Clear visual hierarchy
-   Fast access to important actions
-   Minimal horizontal scrolling
-   Appropriate content prioritization
-   Consistent spacing and typography
-   Accessible controls
-   Smooth transitions between breakpoints

Every major feature in the application must remain usable on mobile,
including:

-   Dashboard
-   Goals
-   Milestones
-   Tasks
-   Habits
-   Learning
-   Calendar
-   Notes
-   Analytics
-   Settings
-   Authentication screens

## 18.2 Mobile-First Consideration

The frontend should be designed with responsive behavior in mind from the
beginning rather than adding responsiveness after the desktop UI is complete.

Components should use flexible layouts and responsive CSS/Tailwind utilities
instead of fixed desktop dimensions wherever practical.

The UI should support common viewport sizes without requiring users to zoom
or rotate their device to complete normal actions.

## 18.3 Navigation

The desktop left sidebar should transform appropriately on smaller screens.

### Desktop / Laptop

Use the existing sidebar navigation:

```text
LifeOS

Dashboard
Goals
Tasks
Habits
Learning
Calendar
Notes
Analytics

Settings
Logout
```

### Tablet

The sidebar may collapse into a compact or collapsible navigation pattern
while preserving quick access to all major modules.

### Mobile

The desktop sidebar should not consume most of the screen width.

A mobile-friendly navigation pattern should be used, such as:

-   Collapsible hamburger menu
-   Compact top navigation
-   Bottom navigation for the most frequently used sections where
    appropriate
-   Accessible menu controls

Navigation should remain easy to operate with one hand and should not hide
essential functionality.

## 18.4 Dashboard Responsiveness

The dashboard layout must adapt to the available screen width.

### Desktop

Use multi-column layouts for:

-   Summary cards
-   Today's Focus
-   Goal Health
-   Habit Tracker
-   Calendar preview
-   Analytics preview
-   LifeOS Insight

### Tablet

Reduce the number of columns and allow cards to stack naturally where
necessary.

### Mobile

Use a single-column or carefully prioritized layout.

The mobile dashboard should prioritize:

1.  Greeting/header
2.  Today's Focus
3.  Important tasks
4.  Goal Health
5.  Habit Summary
6.  LifeOS Insight
7.  Calendar/analytics previews

Large dashboard cards should not force horizontal scrolling.

## 18.5 Responsive Components

All reusable components should be responsive by design.

Examples include:

-   Cards
-   Tables
-   Forms
-   Modals/dialogs
-   Dropdowns
-   Navigation
-   Charts
-   Progress indicators
-   Calendar views
-   Task lists
-   Habit trackers

Components should gracefully change layout or presentation when the screen
becomes smaller.

For example, desktop tables may need to become:

-   Stacked cards
-   Condensed lists
-   Horizontally scrollable only when genuinely necessary
-   Priority-based mobile summaries

The preferred solution should be to redesign the information hierarchy for
mobile rather than forcing users to horizontally scroll through large desktop
tables.

## 18.6 Touch-Friendly Interaction

Mobile users must be able to comfortably interact with the application using
touch input.

Interactive elements should:

-   Have sufficiently large touch targets.
-   Provide adequate spacing between adjacent actions.
-   Avoid tiny icons as the only way to perform important actions.
-   Avoid hover-dependent functionality.
-   Provide clear pressed/focus states where appropriate.
-   Make common actions such as completing a task, logging a habit, or adding
    an item easy to perform.

## 18.7 Forms and Modals

Forms must remain usable on small screens.

Requirements:

-   Form fields should fit within the viewport.
-   Labels and validation messages must remain readable.
-   Buttons should not become cramped.
-   Multi-column desktop forms should stack appropriately on mobile.
-   Modals should adapt to the viewport instead of appearing as oversized
    desktop dialogs.
-   Important actions should remain visible without awkward scrolling.

## 18.8 Charts and Analytics

Analytics visualizations must remain understandable on mobile.

Charts should:

-   Resize according to available width.
-   Avoid clipped labels.
-   Avoid requiring excessive horizontal scrolling.
-   Prioritize the most important metrics on small screens.
-   Use simplified layouts where a complex desktop chart would become
    difficult to read.

If a visualization cannot remain readable at mobile width, an appropriate
mobile-friendly representation such as a summary metric, simplified chart,
or stacked data view should be used.

## 18.9 Calendar Responsiveness

The calendar must remain functional across devices.

Desktop may use a full month/week/day calendar view.

On smaller screens:

-   Calendar controls should remain accessible.
-   Events should remain readable.
-   Day-focused or agenda-style views may be used where more appropriate.
-   Users must still be able to create, edit, and view events without
    requiring desktop-sized screens.

## 18.10 Responsive Breakpoint Validation

The application should be tested at representative viewport sizes across
the following categories:

-   Small mobile phones
-   Large mobile phones
-   Tablets / iPads
-   Laptops
-   Desktop monitors

Testing should verify:

-   No unintended horizontal page scrolling.
-   No overlapping UI elements.
-   No clipped text or controls.
-   No inaccessible buttons.
-   No broken cards or grids.
-   Navigation remains usable.
-   Forms remain usable.
-   Charts remain readable.
-   Core workflows can be completed.

## 18.11 Mobile UX Acceptance Criteria

A responsive implementation is considered successful only when a mobile user
can independently complete the core LifeOS workflow:

```text
Open Application
      ↓
Login / Register
      ↓
View Dashboard
      ↓
Create Goal
      ↓
Create Milestone
      ↓
Create Task
      ↓
Complete Task
      ↓
Log Habit
      ↓
Record Learning Activity
      ↓
View Progress
      ↓
Read Recommendation
```

No step in this workflow should require switching to a desktop device,
zooming excessively, or dealing with a broken/overlapping interface.

## 18.12 Responsive UI Quality Requirement

The mobile experience should feel intentionally designed for mobile rather
than like a compressed desktop application.

The final UI should avoid:

-   Tiny unreadable text
-   Overcrowded cards
-   Desktop-only interactions
-   Excessive horizontal scrolling
-   Fixed-width layouts
-   Overlapping elements
-   Buttons that are difficult to tap
-   Charts that become unreadable
-   Navigation that blocks the content
-   Forms that extend beyond the viewport

The goal is to provide a **professional mobile UX comparable in quality to
the desktop experience**, with appropriate layout and interaction changes for
each device category.

------------------------------------------------------------------------

# 19. MVP Scope

The first complete working version must include:

### Authentication

-   Registration
-   Login
-   Logout

### Dashboard

-   Summary metrics
-   Today's Focus
-   Goal Health
-   Habit summary
-   Insight

### Goals

-   CRUD
-   Milestones
-   Progress
-   Status
-   Life Map (spatial exploration experience within Goal Details)


### Tasks

-   CRUD
-   Priority
-   Deadline
-   Completion
-   Goal association

### Habits

-   CRUD
-   Daily logging
-   Streak
-   Consistency

### Learning

-   Learning items
-   Progress
-   Learning sessions

### Calendar

-   Events
-   Task deadlines

### Notes

-   CRUD
-   Search

### Analytics

-   Goal progress
-   Task completion
-   Habit consistency
-   Learning progress
-   Productivity score

### Intelligence

-   Goal risk
-   Deadline risk
-   Daily Focus
-   Basic recommendations

------------------------------------------------------------------------

# 20. Post-MVP Features

Potential future features:

## AI Assistant

A natural-language assistant could allow:

> "I have three hours today. What should I work on?"

LifeOS could respond using the user's actual tasks, goals, deadlines and
progress.

## Smart Scheduling

Automatically schedule tasks based on:

-   Available time
-   Priority
-   Deadlines
-   Estimated effort

## Calendar Integration

Integration with external calendar providers.

## Advanced Analytics

-   Productivity trends
-   Weekly reports
-   Monthly reports
-   Goal completion probability

## Personalized Learning Plans

Automatically generate learning sequences based on target skills.

## Notifications

-   Task reminders
-   Habit reminders
-   Deadline warnings
-   Goal-risk alerts

------------------------------------------------------------------------

# 21. Success Criteria

LifeOS will be considered a successful MVP when a user can complete the
following journey:

``` text
Register
   ↓
Create Goal
   ↓
Create Milestones
   ↓
Create Tasks
   ↓
Complete Tasks
   ↓
Track Habits
   ↓
Record Learning
   ↓
View Dashboard
   ↓
View Analytics
   ↓
Receive Recommendation
```

The system should demonstrate that activity in one module can influence
information shown in another module.

Example:

``` text
Incomplete Tasks
       ↓
Lower Task Completion
       ↓
Lower Productivity Score
       ↓
Goal Progress Falls Behind
       ↓
Goal Becomes At Risk
       ↓
Recommendation Generated
```

This interconnected behavior is the defining characteristic of LifeOS.

------------------------------------------------------------------------

# 22. Technical Demonstration Value

LifeOS should demonstrate knowledge of:

-   React.js
-   Component-based UI development
-   REST API design
-   Java
-   Spring Boot
-   Spring Data JPA
-   Spring Security
-   MySQL
-   Relational database design
-   Authentication
-   CRUD operations
-   Business logic
-   Analytics
-   Rule-based recommendation systems
-   Error handling
-   Responsive UI
-   Full-stack architecture

------------------------------------------------------------------------

# 23. Project Architecture

``` text
                        ┌──────────────────────┐
                        │        USER          │
                        └──────────┬───────────┘
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │    React Frontend    │
                        │                      │
                        │ Dashboard            │
                        │ Goals                │
                        │ Tasks                │
                        │ Habits               │
                        │ Learning             │
                        │ Calendar             │
                        │ Notes                │
                        │ Analytics             │
                        └──────────┬───────────┘
                                   │
                              REST APIs
                                   │
                                   ▼
                        ┌──────────────────────┐
                        │   Spring Boot API    │
                        ├──────────────────────┤
                        │ Controllers          │
                        │ Services             │
                        │ Business Logic       │
                        │ Security             │
                        └──────────┬───────────┘
                                   │
                 ┌─────────────────┼─────────────────┐
                 │                 │                 │
                 ▼                 ▼                 ▼
          ┌─────────────┐  ┌──────────────┐  ┌───────────────┐
          │  Analytics  │  │Recommendation│  │ Authentication │
          │   Engine    │  │    Engine    │  │    / Security  │
          └──────┬──────┘  └──────┬───────┘  └───────────────┘
                 │                │
                 └────────┬───────┘
                          │
                          ▼
                  ┌───────────────┐
                  │     MySQL     │
                  ├───────────────┤
                  │ Users         │
                  │ Goals         │
                  │ Milestones    │
                  │ Tasks         │
                  │ Habits        │
                  │ Learning      │
                  │ Events        │
                  │ Notes         │
                  └───────────────┘
```

------------------------------------------------------------------------

# 24. Development Strategy

LifeOS should be built in vertical slices rather than completing the
entire frontend first and the backend afterward.

Recommended order:

## Phase 1 - Foundation

-   Repository setup
-   React project
-   Spring Boot project
-   MySQL connection
-   Environment configuration
-   Basic API structure

## Phase 2 - Authentication

-   User entity
-   Registration
-   Login
-   Security
-   Protected routes

## Phase 3 - Goals + Milestones

-   Database model
-   Backend APIs
-   Frontend pages
-   Goal progress

## Phase 4 - Tasks

-   Task CRUD
-   Priorities
-   Deadlines
-   Goal association
-   Completion tracking

## Phase 5 - Habits

-   Habit CRUD
-   Habit logs
-   Streak calculations
-   Consistency metrics

## Phase 6 - Learning

-   Learning items
-   Learning sessions
-   Progress tracking

## Phase 7 - Dashboard

Connect all existing modules into one dashboard.

## Phase 8 - Analytics

Build the analytics calculations and visualizations.

## Phase 9 - Recommendation Engine

Implement explainable rule-based recommendations.

## Phase 10 - UI/UX Polish

-   Responsive design
-   Loading states
-   Empty states
-   Error states
-   Animations
-   Accessibility
-   Final visual refinement

------------------------------------------------------------------------

# 25. Definition of Done

A feature is considered complete only when:

-   Database model exists.
-   Backend entity/repository/service/controller are implemented where
    applicable.
-   API is tested.
-   Frontend UI exists.
-   Frontend communicates with the API.
-   Validation exists.
-   Error handling exists.
-   Loading/empty states exist.
-   User authorization is respected.
-   The feature works end-to-end.

A button that merely looks functional is not a feature. Humanity has
enough fake buttons.

------------------------------------------------------------------------

# 26. Final Product Principle

LifeOS should always follow one core principle:

> **Every action should connect back to a meaningful outcome.**

A task should not merely be a task.

It can contribute to:

**Task → Milestone → Goal → Life Outcome**

Likewise:

**Habit → Consistency → Productivity → Goal Progress**

And:

**Activity Data → Analytics → Insight → Recommendation → Better Action**

That interconnected loop is what separates LifeOS from a basic
collection of productivity CRUD modules.

------------------------------------------------------------------------

# 27. MVP Product Statement

**LifeOS is a full-stack personal operating system that unifies goal
management, task management, habit tracking, learning progress,
scheduling, and productivity analytics while using a rule-based
recommendation engine to help users prioritize actions and identify
goals that are at risk.**
