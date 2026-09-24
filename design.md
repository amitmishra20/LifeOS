# LifeOS Design System & Product Experience Specification

**Document:** `design.md`\
**Product:** LifeOS --- Your Life, Organized. Focused. Growing.\
**Status:** Implementation Source of Truth\
**Version:** 1.0

------------------------------------------------------------------------

## 1. Purpose

This document defines the visual language, interaction behavior,
responsive experience, component system, motion principles, and product
experience standards for LifeOS.

It complements the other project documents:

-   `prd.md` --- defines **WHAT** LifeOS does.
-   `architecture.md` --- defines **HOW** LifeOS is technically
    structured.
-   `rules.md` --- defines engineering and product constraints.
-   `design.md` --- defines **HOW LifeOS looks, feels, behaves, and
    communicates**.

When implementing UI, `design.md` is the design source of truth.

------------------------------------------------------------------------

## 2. Design Vision

LifeOS should feel like an **operating system for a person's life**, not
a collection of productivity pages.

The experience should communicate:

> **Clean + Premium + Intelligent + Fluid + Slightly Unexpected.**

LifeOS should feel calm, focused, mature, personal, intelligent,
refined, slightly futuristic, trustworthy, and useful.

It should not feel childish, noisy, overly gamified, corporate
SaaS-like, AI-generated, overloaded with dashboards, or dependent on
visual effects to appear premium.

The design should make complexity feel simple.

------------------------------------------------------------------------

## 3. Core Design Philosophy

### Powerful underneath, simple on the surface

LifeOS may contain sophisticated relationships between goals,
milestones, tasks, habits, learning, progress, analytics, and
recommendations. The interface should not force the user to understand
that complexity.

Expose complexity progressively.

### Clarity before decoration

Every visual element must have a purpose. Prefer hierarchy, whitespace,
typography, alignment, restrained color, and meaningful depth over
decorative effects.

### Calm productivity

LifeOS should reduce cognitive load rather than increase it.

Avoid notification spam, excessive badges, excessive colors, unnecessary
alerts, aggressive streak mechanics, guilt-inducing incomplete-task
indicators, excessive configuration, and information overload.

### User control

Recommendations should assist the user rather than silently control
their life. Make clear:

-   what LifeOS observed
-   why it matters
-   what it recommends
-   what action the user can take

Important user data must never be silently modified by a recommendation.

### Product identity over trend-following

LifeOS may use modern interaction and visual techniques, but it must not
become a collection of trends. No effect should exist merely because it
looks impressive.

------------------------------------------------------------------------

## 4. Experience Loop

LifeOS's core experience should reinforce:

``` text
Goal
  ↓
Milestone
  ↓
Task / Action
  ↓
Execution
  ↓
Progress
  ↓
Reflection
  ↓
Recommendation
  ↓
Next Focus
```

The interface should make these relationships visible where useful.

A user should be able to understand:

> What am I trying to achieve?\
> What matters now?\
> What should I do next?\
> How am I progressing?\
> What should I change?

------------------------------------------------------------------------

## 5. The 3-Second Rule

Within approximately three seconds of opening a major page, the user
should understand:

1.  What this page is for.
2.  What matters most right now.
3.  What action they can take next.

Avoid pages where the user must scan many cards before understanding the
purpose.

Each major page needs one clear primary purpose.

------------------------------------------------------------------------

## 6. Information Hierarchy

Use:

``` text
Page purpose
    ↓
Primary context
    ↓
Important information
    ↓
Primary action
    ↓
Secondary information
    ↓
Details
```

Create hierarchy primarily through typography, spacing, position,
grouping, and contrast.

Do not use large shadows, giant cards, or gradients as substitutes for
hierarchy.

------------------------------------------------------------------------

## 7. Navigation Philosophy

Navigation should be organized around user intent.

``` text
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
```

### Desktop

Use a compact persistent sidebar with clear text labels, recognizable
icons, an obvious active state, consistent spacing, and minimal
decoration.

### Tablet

The sidebar may collapse or become a compact navigation system depending
on available width.

### Mobile

Mobile navigation must be intentionally designed rather than simply
shrinking desktop navigation.

Priority:

``` text
Capture → Focus → Execute
```

Use a compact top navigation, contextual menus, and bottom navigation
where appropriate. Do not overload mobile navigation.

------------------------------------------------------------------------

## 8. Layout System

Use the spacing scale:

``` text
4
8
12
16
24
32
48
64
96
```

Avoid arbitrary spacing unless a specific visual requirement justifies
it.

Principles:

-   generous whitespace
-   aligned related content
-   predictable vertical rhythm
-   no overcrowding
-   fluid layouts
-   no unnecessary fixed widths
-   no normal-workflow horizontal scrolling

------------------------------------------------------------------------

## 9. Responsive Design

Responsive design is a first-class requirement.

LifeOS must work across:

-   desktop
-   laptop
-   tablet
-   iPad
-   mobile

Mobile is not a secondary version of desktop.

Components must adapt to available space, including navigation,
dashboards, cards, tables, forms, modals, drawers, dropdowns, charts,
progress indicators, calendars, task lists, habit trackers, and
analytics.

Mobile interfaces must use touch-friendly controls, avoid tiny text and
cramped controls, avoid hover dependencies, preserve hierarchy, and
avoid horizontal overflow.

Every major workflow must be usable on mobile.

------------------------------------------------------------------------

## 10. Visual Identity

Use:

> **Graphite / Midnight foundation + Deep Indigo primary accent + restrained
> semantic colors.**

Confirmed palette tokens:

-   **Foundation:** Graphite / Midnight (neutral, restrained dark and light surfaces)
-   **Primary Accent:** Deep Indigo (clear brand anchor without visual noise)
-   **Semantic Indicators:**
    -   Emerald for healthy / completed states
    -   Amber for warning / at-risk states
    -   Crimson for behind / critical / overdue states

All values must be centralized as design tokens. Do not introduce excessive
gradients, neon, glow, glassmorphism, or decorative visual effects.


------------------------------------------------------------------------

## 11. Color System

Use semantic design tokens rather than hard-coded colors.

Suggested token groups:

``` text
background
surface
surface-subtle
surface-elevated
border
text-primary
text-secondary
text-muted
accent
accent-subtle
success
warning
danger
info
focus
```

Semantic colors must communicate meaning consistently.

Accent color should establish LifeOS identity without overwhelming the
interface.

------------------------------------------------------------------------

## 12. Surfaces, Texture, and Depth

Premium appearance should come from controlled depth.

Use:

-   subtle grain where appropriate
-   soft shadows
-   low-contrast borders
-   restrained translucency
-   subtle surface variation
-   gentle light diffusion
-   layered surfaces

Avoid:

-   glassmorphism everywhere
-   glowing borders
-   huge blurred backgrounds
-   excessive transparency
-   floating blobs
-   strong neon effects

Depth should communicate hierarchy.

------------------------------------------------------------------------

## 13. Typography

Typography must establish a clear hierarchy:

``` text
Display / Page title
Section heading
Card/feature heading
Body
Secondary text
Metadata
```

Prioritize readability, consistent scale, appropriate line height, clear
weight hierarchy, and strong contrast.

Avoid excessive weights, tiny secondary text, overly compressed
typography, and decorative fonts that reduce readability.

------------------------------------------------------------------------

## 14. Radius System

Use a small set of consistent radius tokens:

``` text
small
medium
large
pill
```

Do not give every element a different arbitrary radius.

Avoid turning the application into a collection of oversized rounded
cards.

------------------------------------------------------------------------

## 15. Shadows and Depth

Shadows should be subtle and purposeful.

Use depth primarily for elevated panels, dialogs, sheets, floating
controls, and important interactive surfaces.

Avoid heavy shadows everywhere, dramatic drop shadows, glowing shadows,
and competing elevation systems.

------------------------------------------------------------------------

## 16. Component Design System

Build reusable primitives before repeatedly creating custom UI.

Core components should include where needed:

-   Button
-   IconButton
-   Input
-   Textarea
-   Select
-   Checkbox
-   Radio
-   Toggle
-   Tabs
-   Badge
-   Tooltip
-   Card/Surface
-   Modal/Dialog
-   Drawer/Sheet
-   Dropdown/Menu
-   Progress
-   Avatar
-   EmptyState
-   LoadingState
-   ErrorState
-   Toast/Feedback
-   DataTable
-   List
-   Calendar primitives
-   Chart containers

All components must use shared design tokens.

A new feature should reuse existing components instead of creating
inconsistent one-off versions.

------------------------------------------------------------------------

## 17. Component States

Relevant interactive components should intentionally define:

``` text
default
hover
focus
active
disabled
loading
error
success
```

Keyboard focus must remain visible.

------------------------------------------------------------------------

## 18. Dashboard Experience

The Dashboard is the user's home.

Its purpose is not to display everything. Its purpose is to answer:

> **What matters in my life right now?**

Prioritize:

-   today's focus
-   meaningful progress
-   important goals
-   upcoming commitments
-   relevant insights
-   useful quick actions

Avoid filling the dashboard with every available metric.

------------------------------------------------------------------------

## 19. Today's Focus

Today's Focus should reduce an overwhelming task list into a manageable
set of meaningful actions.

It should answer:

``` text
What should I focus on?
Why does it matter?
What can I do now?
```

Prioritization should remain understandable.

Avoid turning Today's Focus into a gamified score screen.

------------------------------------------------------------------------

## 20. Goals

Goals should feel strategic.

A goal should communicate:

-   desired outcome
-   current state
-   progress
-   milestones
-   associated actions
-   relevant timeline
-   health/context where available

Goal pages should visually connect the long-term outcome with near-term
execution.

------------------------------------------------------------------------

## 21. Life Map

Life Map is a signature LifeOS interaction. For MVP, it is implemented as a
spatial exploration experience within Goal Details.

Concept:

``` text
Goal
 ↓
Milestones
 ↓
Tasks
 ↓
Actions
```

The interface allows the user to explore this hierarchy spatially:

-   Goal expands to reveal milestones.
-   Selecting a milestone reveals associated tasks.
-   Selecting a task reveals actionable execution details.
-   Transitions preserve spatial context rather than jumping across pages.

Constraints:
-   Do NOT build a complex node-editor or canvas system for MVP.
-   Do NOT introduce unnecessary graph-editing functionality.
-   Keep the component architecture extensible for a richer dedicated Life Map experience later.

------------------------------------------------------------------------

## 22. Tasks

Tasks should prioritize speed and clarity.

Users should be able to create tasks quickly, understand priority and
due dates, associate tasks with goals/milestones, complete tasks with
minimal friction, and edit tasks without excessive dialogs.

Task completion should feel satisfying but not theatrical.

Avoid excessive confirmation dialogs.

------------------------------------------------------------------------

## 23. Habits

Habits should encourage consistency without becoming punitive.

Avoid aggressive streak mechanics, guilt messages, excessive
celebration, and visual punishment for missed days.

Focus on consistency, trends, context, sustainable behavior, and
progress over perfection.

------------------------------------------------------------------------

## 24. Learning

Learning should feel progressive and structured.

Help users understand:

-   what they are learning
-   what they have completed
-   what is next
-   how learning connects to goals
-   where progress is happening

Avoid meaningless progress bars. Progress must correspond to real user
activity.

------------------------------------------------------------------------

## 25. Calendar

Calendar should feel spatial and time-oriented.

Clearly communicate dates, commitments, tasks, relevant events, and time
relationships.

Calendar interactions must remain usable on touch devices.

Avoid overly dense mobile calendar layouts.

------------------------------------------------------------------------

## 26. Notes

Notes should be quiet and distraction-free.

Prioritize readable typography, easy capture, easy editing,
organization, and search where appropriate.

Do not overload notes with unnecessary productivity controls.

------------------------------------------------------------------------

## 27. Analytics

Analytics should answer questions rather than simply display data.

Useful analytics may communicate:

-   progress trends
-   execution consistency
-   goal movement
-   habit consistency
-   learning progress
-   workload patterns

Every chart must have a reason to exist and remain readable on mobile.

------------------------------------------------------------------------

## 28. Goal Health

Goal Health should communicate whether a goal appears:

-   progressing normally
-   requiring attention
-   losing momentum
-   blocked
-   inconsistent

The presentation should explain the underlying signals.

Avoid opaque scores with no context.

------------------------------------------------------------------------

## 29. Recommendation Experience

Recommendations should feel like LifeOS noticed something useful.

Preferred structure:

``` text
Observation
    ↓
Why it matters
    ↓
Suggested focus
    ↓
Optional action
```

The UI should explain the observation, provide context, suggest a
possible next action, and let the user decide.

Avoid generic motivational statements, unexplained scores, excessive
recommendation cards, and notification spam.

------------------------------------------------------------------------

## 30. Quick Capture

Quick capture should support lightweight inputs such as tasks, notes,
ideas, and reminders where appropriate.

The user should be able to capture information without navigating
through several screens.

Keep it simple.

------------------------------------------------------------------------

## 31. Forms

Forms should minimize cognitive load.

Principles:

-   clear labels
-   useful defaults
-   logical grouping
-   inline validation
-   meaningful errors
-   mobile-friendly controls
-   minimal required fields
-   preserve user input after validation errors

Do not ask users for information LifeOS does not need.

------------------------------------------------------------------------

## 32. Modals and Drawers

Use modals for focused actions and drawers/sheets when users need to
preserve context.

Avoid stacking dialogs.

Avoid using a modal when a normal page or inline interaction is clearer.

------------------------------------------------------------------------

## 33. Loading States

Use skeletons where structure is known, subtle progress indicators, and
immediate feedback for actions.

Avoid excessive shimmer.

Loading states should preserve layout stability where possible.

------------------------------------------------------------------------

## 34. Empty States

Empty states should explain:

1.  What is missing.
2.  Why it matters.
3.  What the user can do next.

Avoid screens that simply say "No data."

------------------------------------------------------------------------

## 35. Error States

Errors should be understandable, actionable, calm, and specific where
possible.

Never expose raw backend errors or stack traces to users.

Where recovery is possible, provide an appropriate recovery action.

------------------------------------------------------------------------

## 36. Microinteractions

Microinteractions should provide feedback for actions such as completing
a task, saving data, changing state, opening a panel, switching context,
and successful actions.

They should be subtle.

Do not animate every interaction.

------------------------------------------------------------------------

## 37. Motion System

LifeOS motion should feel native-quality and natural, inspired by
polished iOS/macOS-like interactions.

Suggested timing:

``` text
Micro interaction:     120–200ms
Small transition:      180–250ms
Panel/sheet:           220–350ms
Major transformation:  350–600ms
```

Use natural easing.

Avoid bounce-heavy animation, random springs, slow transitions, dramatic
zooms, animation that blocks interaction, and repeated
attention-grabbing effects.

Respect reduced-motion preferences.

------------------------------------------------------------------------

## 38. Signature Motion

LifeOS should have a small number of recognizable motion patterns rather
than dozens of unrelated animations.

### Life Map transition

Goal → Milestone → Task should feel like moving deeper into the same
context rather than jumping between unrelated pages.

### Today's Focus

Focus items can transition into execution context with subtle movement
preserving spatial relationship.

### Recommendation reveal

Recommendations should appear with calm, contextual transitions.

------------------------------------------------------------------------

## 39. External Design Resources

LifeOS may use these resources for inspiration and implementation
acceleration:

-   `60fps.design` --- motion and interaction inspiration
-   `21st.dev` --- React component and UI pattern references
-   `glass3d.dev` --- selective 3D/depth inspiration

### 60fps.design

Use for motion, gestures, transitions, shared-element ideas,
reveal/slide/swipe patterns, and polished state changes.

### 21st.dev

Use for React component references, UI patterns, reusable component
ideas, and implementation acceleration.

### glass3d.dev

Use selectively for depth, 3D accents, special visual treatments, and
premium visual experiments.

### Non-negotiable rule

> **External resources may inspire or accelerate implementation, but
> LifeOS must maintain a single coherent visual language.**

No external component, animation, 3D element, or visual effect should be
integrated without adapting it to the LifeOS design system.

Do not blindly copy components.

Do not allow external resources to override LifeOS design decisions.

------------------------------------------------------------------------

## 40. Accessibility

Accessibility is part of the design system.

Requirements include:

-   sufficient contrast
-   visible keyboard focus
-   semantic HTML where appropriate
-   accessible labels
-   keyboard navigation
-   touch-friendly controls
-   meaningful error messages
-   reduced-motion support
-   usable form controls
-   no information conveyed through color alone

Accessibility must be considered during component implementation.

------------------------------------------------------------------------

## 41. Dark Mode

Dark mode must be intentionally designed.

Do not simply invert colors.

Maintain hierarchy, contrast, readability, depth, semantic meaning, and
restrained accent usage.

Use controlled tonal differences rather than making every surface pure
black.

------------------------------------------------------------------------

## 42. AI-Slop Prevention

LifeOS must not look like a generic AI-generated application.

Avoid:

-   excessive purple gradients
-   glowing borders
-   giant rounded cards
-   floating blobs
-   excessive glassmorphism
-   meaningless AI labels
-   "AI-powered" text everywhere
-   generic dashboard templates
-   repetitive card grids
-   decorative charts with no purpose
-   random icons
-   inconsistent spacing
-   arbitrary component styles
-   unnecessary futuristic effects

AI coding tools must implement the established LifeOS system rather than
inventing new visual patterns.

------------------------------------------------------------------------

## 43. Anti-Frustration Design

Avoid:

-   excessive confirmation dialogs
-   unnecessary multi-step flows
-   repetitive data entry
-   forced onboarding before useful functionality
-   hidden important actions
-   excessive notifications
-   destructive actions without appropriate safeguards
-   requiring users to configure every detail

Provide sensible defaults.

------------------------------------------------------------------------

## 44. Sustainable Engagement

LifeOS should encourage continued use through usefulness.

Do not rely on addictive mechanics, punishment, guilt, notification
pressure, artificial scarcity, or manipulative streaks.

The goal is:

> **Useful enough that the user wants to return.**

------------------------------------------------------------------------

## 45. Feature-Level Design Architecture

Every new feature should follow:

``` text
Requirement
    ↓
User intent
    ↓
Information hierarchy
    ↓
Interaction model
    ↓
Responsive behavior
    ↓
Component selection
    ↓
Visual implementation
    ↓
Loading / Empty / Error states
    ↓
Accessibility
    ↓
Motion
    ↓
Review
```

Before creating a new component, check whether an existing shared
component can be reused.

------------------------------------------------------------------------

## 46. Design System Architecture

Suggested frontend structure:

``` text
frontend/src/
├── design-system/
│   ├── tokens/
│   │   ├── colors
│   │   ├── typography
│   │   ├── spacing
│   │   ├── radius
│   │   ├── shadows
│   │   └── motion
│   ├── components/
│   └── utilities/
├── features/
├── layouts/
├── pages/
├── hooks/
├── services/
└── utils/
```

The exact implementation may differ according to the frontend
architecture, but the principles remain.

------------------------------------------------------------------------

## 47. Design Quality Gates

Before UI is complete, verify:

### Product clarity

-   purpose is immediately understandable
-   primary action is obvious
-   feature reduces cognitive load

### Visual quality

-   hierarchy is clear
-   spacing is consistent
-   typography and colors are consistent
-   it looks like LifeOS

### Responsive quality

-   desktop tested
-   tablet tested
-   mobile tested
-   no horizontal overflow
-   no clipped content
-   touch interactions usable

### Interaction quality

-   loading state
-   empty state
-   error state
-   success feedback
-   appropriate motion

### Accessibility

-   keyboard accessibility
-   focus states
-   contrast
-   semantic structure
-   accessible labels

### Engineering consistency

-   existing components reused where appropriate
-   design tokens used
-   no arbitrary styles
-   no duplicated design-system logic

------------------------------------------------------------------------

## 48. Vibe-Coding Rules for Design

When using AI coding agents:

1.  Read `design.md` before implementing UI.
2.  Reuse existing components.
3.  Reuse existing design tokens.
4.  Do not invent new colors without justification.
5.  Do not invent new radius systems.
6.  Do not invent new typography systems.
7.  Do not add animation without purpose.
8.  Do not introduce external UI components without adaptation.
9.  Test responsive behavior.
10. Test loading, empty, error, and success states.
11. Preserve accessibility.
12. Do not convert every feature into cards.
13. Do not add decorative elements just to fill whitespace.
14. Do not optimize for screenshots over actual usability.

If a design decision conflicts with this document, stop and explain the
conflict before making a structural change.

------------------------------------------------------------------------

## 49. Relationship Between Project Documents

``` text
PRD
"What are we building?"
        ↓
Architecture
"How should it technically work?"
        ↓
Rules
"What constraints must implementation follow?"
        ↓
Design
"How should it look, feel, and behave?"
        ↓
Implementation
"Build it consistently."
```

No document should silently override another.

If a genuine conflict appears:

1.  Identify the conflict.
2.  Explain its impact.
3.  Propose options.
4.  Update the appropriate source document after the decision.

------------------------------------------------------------------------

## 50. Product Signature

LifeOS should become recognizable through the combination of:

-   calm hierarchy
-   strong typography
-   restrained premium surfaces
-   intentional whitespace
-   subtle depth
-   intelligent information prioritization
-   spatial relationships between goals and actions
-   purposeful motion
-   responsive mobile experience
-   contextual recommendations
-   minimal visual noise

The product should not need a logo or label to feel like LifeOS.

------------------------------------------------------------------------

## 51. Non-Negotiable Design Rules

1.  **Clarity before decoration.**
2.  **Mobile is first-class.**
3.  **Use design tokens consistently.**
4.  **Do not create unnecessary visual complexity.**
5.  **Motion must communicate or provide feedback.**
6.  **External inspiration must be adapted to LifeOS.**
7.  **Do not blindly copy 21st.dev components.**
8.  **Use 60fps.design primarily for interaction inspiration.**
9.  **Use Glass3D selectively, not everywhere.**
10. **Avoid generic AI/SaaS aesthetics.**
11. **Do not turn every UI section into a card.**
12. **Every chart must communicate something useful.**
13. **Every recommendation must provide understandable context.**
14. **Do not use guilt-based engagement mechanics.**
15. **Important workflows must work on mobile.**
16. **Accessibility is part of implementation.**
17. **Loading, empty, error, and success states are part of feature
    completion.**
18. **Reuse existing components before creating new ones.**
19. **Do not sacrifice usability for visual novelty.**
20. **Powerful underneath, simple on the surface.**

------------------------------------------------------------------------

## 52. Final Design Philosophy

LifeOS should feel like a system that understands the structure of a
person's life without pretending to control it.

It should help users move from:

``` text
"I have too much going on."
        ↓
"I understand what matters."
        ↓
"I know what to do next."
        ↓
"I can see my progress."
        ↓
"I understand what needs to change."
```

The interface should remain calm while the underlying system remains
powerful.

**LifeOS is not designed to make people spend more time inside the
application.**

It is designed to help people spend their time better outside it.

> **Clean enough to understand.\
> Powerful enough to matter.\
> Intelligent enough to help.\
> Calm enough to trust.**
