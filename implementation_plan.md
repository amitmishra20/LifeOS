# Phase 3 — Finalized Implementation Plan v2
**LifeOS Frontend Visual + Interaction Foundation**

> **Document Status:** Under User Review (Finalized with Corrections)  
> **Source of Truth:** `prd.md`, `architecture.md`, `rules.md`, `design.md`  
> **Target:** Cinematic, calm, premium, editorial, human, visual, spatial, and fluid personal operating environment.  

---

## 1. Executive Summary

This document reconciles and replaces the initial Phase 3 implementation plan. 

While previous Phase 3 work established essential technical scaffolds (routing, component skeletons, mock data contracts, RFC-7807 error unwrap, and diagnostic routing), the resulting visual treatment skewed too close to a **generic dark SaaS productivity dashboard**:
- Saturated indigo and purple accent dominance (`#4F46E5`, `#6366F1`)
- Pervasive card-grid packaging ("boxes inside boxes")
- High status badge density and widget-like KPI counters
- Symmetrical, conventional dashboard widgets

**We explicitly acknowledge that the current implementation does not satisfy the newly approved visual identity.** The current codebase represents a technical starting point that will now be systematically refactored at the system level.

LifeOS is not a SaaS project management tool or an analytics console; it is **"a personal operating environment for your life."** Phase 3 Plan v2 defines how LifeOS achieves a distinctive, calm, cinematic, and editorial presence while strictly preserving Phase 3 scope boundaries (frontend visual and interaction foundation only; zero backend or database mutations).

---

## 2. What Changes from the Previous Phase 3 Plan

| Dimension | Previous Phase 3 Plan | Revised Phase 3 Plan v2 |
| :--- | :--- | :--- |
| **Color Philosophy** | Midnight navy foundation (`#070A11`, `#0E1422`, `#12192B`) with heavy indigo/purple accents (`#4F46E5`). | **Deep Black / Charcoal foundation** with restrained warm cinematic highlights, subtle teal/emerald momentum, and controlled contrast (initial tokens refined in-browser). |
| **Composition & Layout** | Rigid 65% / 35% two-column card grid widget container. | **Open, asymmetrical editorial layout**: dynamic visual anchors, timelines, negative space, and integrated journeys without wrapping every element in a card. |
| **Structural Units** | Cards as the default container for all sections. | **Selective surfaces**: Open sections, layered planes, subtle divider rules, and spatial paths. Cards are used only where strict containment is required. |
| **Information Density** | High badge density: Multiple status pills, category tags, deadline tags, and KPI gauges on every row. | **Progressive disclosure**: "Show what matters now, reveal details when needed." Clean typographic hierarchy; removal of redundant badges and labels. |
| **Theming** | Dark mode only, hardcoded dark custom properties. | **Architected Dual-Theme System**: Deep Black as default + refined Light counterpart sharing 100% of semantic design tokens. |
| **Visual Metaphor** | Database items (Goal cards, Task lists, Habit cards). | **Spatial Journeys**: Goals as destinations, Milestones as checkpoints, Tasks as actions along the path, Today's Focus as current position. |
| **Motion Language** | Standard 150–300ms hover lifts and modal fades. | **Spatial continuity & morphing**: Elements expand from their triggers; tasks animate momentum into milestone progress; smooth spring curves. |
| **Intelligence Presentation** | "DAILY FOCUS ENGINE", "AI-Powered", "100% Confidence" badges. | **Quiet contextual intelligence**: Natural editorial observations ("Observation → Why It Matters → Suggested Focus → Action") with zero AI buzzwords. |
| **Mock Strategy** | Synthetic persona-driven mock data. | **Persona-agnostic, realistic LifeOS demonstration data** spanning career, learning, personal craft, and fitness routines. |

---

## 3. What Remains Unchanged & Strict Scope Boundaries

1. **Technology Stack**: React 19.2+, Vite, Vanilla CSS with native CSS Custom Properties. Zero runtime CSS-in-JS dependencies.
2. **Canonical 6 Navigation Groups**: `HOME`, `FOCUS`, `PLAN`, `GROW`, `REFLECT`, `CAPTURE` as defined in `design.md`.
3. **Strict Scope Boundaries & Prototype Nature (Rule 26)**:
   The Dashboard will visually demonstrate Today's Focus, Goal → Milestone → Task journey, Habit rhythm, Recommendation, and Progress/momentum. However, these are **strictly frontend visual and interaction prototypes driven by centralized mock data**.
   Phase 3 will **NOT** implement:
   - Real Task business logic or scheduling algorithms
   - Real Habit business logic or streak tracking rules
   - Real Goal / Milestone CRUD operations
   - Real recommendation engine or predictive models
   - Real deterministic progress calculation services
   - Any new backend APIs or controller endpoints
   - Any database schema changes or migrations
   - Any authentication or session functionality  
   *All business logic and persistence belong strictly to future phases.*
4. **Mock Data Layer**: Centralized, realistic mock data contracts in `src/mocks/dashboardMockData.js` ready for future backend consumption.
5. **Phase 1 Diagnostic Route**: Preservation of `FoundationPage.jsx` at `/dev/foundation` to verify backend connectivity, RFC-7807 error unwrap, and viewport diagnostics.
6. **Accessible Component Primitives**: High-quality primitives (`Button`, `Input`, `Select`, `Checkbox`, `Toggle`, `Modal`, `Drawer`, `Tabs`, `Tooltip`, `ProgressBar`) remain the building blocks, but refactored to shed SaaS styling.

---

## 4. Final Visual North Star

LifeOS must feel:
```
CINEMATIC · CALM · PREMIUM · EDITORIAL · HUMAN · VISUAL · SPATIAL · INTELLIGENT · FLUID · DISTINCTIVE · SLIGHTLY UNEXPECTED
```

### The Primary Design Test
> **"If I removed the LifeOS logo and product name, would this still look like a generic SaaS dashboard?"**  
> If the answer is yes, the design has failed.

LifeOS achieves its identity through:
- **Atmospheric Depth**: A quiet, spacious environment that invites deep reflection and calm action.
- **Editorial Cadence**: Typography that feels like a curated journal or architectural monograph rather than software telemetry.
- **Spatial Storytelling**: Showing the trajectory of one's life through visible horizons, checkpoints, and daily steps.
- **Restraint**: Elegance through subtraction. Eliminating border outlines, decorative badges, and saturated glows.

---

## 5. Dark Theme Architecture (Default)

The dark theme is the primary operating environment of LifeOS. It is built for sustained use (8–12 hours daily) without visual fatigue.

### Surface Tiers & Spatial Physics
- **Void Canvas (`--surface-canvas`: `#050507` initial)**: The deepest environment plane. Absorbs eye strain; provides infinite visual depth.
- **Base Canvas (`--surface-base`: `#090A0D` initial)**: The primary workspace floor where page content lives.
- **Quiet Surface (`--surface-subtle`: `#0F1015` initial)**: Secondary groupings, subtle insets, and non-elevated sections.
- **Tactile Surface (`--surface-raised`: `#14161D` initial)**: Interactive items, floating toolbars, and contextual cards.
- **Overlay Surface (`--surface-overlay`: `rgba(14, 16, 23, 0.82)` initial)**: Modals, slide sheets, and command palette with `backdrop-filter: blur(20px) saturate(160%)`.

### Edge Definition & Light Physics
- **No glowing borders**: Borders are razor-thin (1px) and whisper-quiet:
  - Subtle separator: `rgba(255, 255, 255, 0.05)`
  - Active boundary: `rgba(255, 255, 255, 0.10)`
  - Top-edge ambient sheen: `rgba(255, 255, 255, 0.12)` (simulating soft top-down ambient light)
- **Ambient Lighting**: Very soft, warm atmospheric radial falloff in hero context (`radial-gradient(ellipse at 50% -10%, rgba(212, 163, 115, 0.05), transparent 70%)`). Zero neon blue or electric purple blobs.

---

## 6. Light Theme Architecture

The Light Theme is the architectural counterpart to the Deep Black system. It shares identical structural proportions, typography, layout geometry, and spatial relationships.

```
       [Semantic Token Layer]
                 │
   ┌─────────────┴─────────────┐
   ▼                           ▼
[Deep Black Theme]     [Light Theme]
(Default: :root)       ([data-theme="light"])
#050507 canvas         #F7F7F5 bone canvas
#14161D surface        #FFFFFF pure surface
rgba(255,255,255,0.08) rgba(0,0,0,0.06) border
```

### Light Palette Specifications
- **Canvas (`--surface-canvas`: `#F5F5F3` initial)**: Warm bone / alabaster. Avoids clinical, glaring pure white on full viewport backdrops.
- **Base Workspace (`--surface-base`: `#FAFAFA` initial)**: Calm reading foundation.
- **Pure Surface (`--surface-raised`: `#FFFFFF` initial)**: Tactile interactive surfaces with soft, multi-layered contact shadows.
- **Borders & Rules**: Warm slate borders (`rgba(0, 0, 0, 0.06)` to `rgba(0, 0, 0, 0.12)`).
- **Text & Foreground**:
  - Primary text: `#121316` (deep charcoal, avoiding harsh `#000000`)
  - Secondary text: `#575B66`
  - Muted text: `#8A8F9E`

### Switching Architecture
- Pure CSS token re-mapping applied via `<html data-theme="dark|light">`.
- Zero flash of unstyled theme (FOUT) with instant attribute hydration.
- Components consume exclusively semantic tokens (`var(--surface-base)`, `var(--text-primary)`, `var(--border-subtle)`).

---

## 7. Color & Token Strategy

Color in LifeOS communicates **meaning, state, and momentum**, never decoration.

### Initial Tokens & Refinement Principle
The specific HEX values specified in this plan are **INITIAL DESIGN TOKENS**. They serve as our initial baseline and will be evaluated and subtly refined during visual implementation in the live browser interface to achieve optimal contrast, luminescence, and harmony. 

**Guardrail:** Refinements will strictly adhere to the Deep Black + warm cinematic highlights + restrained semantic direction. Under no circumstances will any refinement revert to the previous saturated indigo or purple-heavy aesthetic.

### Initial Semantic Palette
```css
/* Core Accents */
--accent-warm: #D4A373;          /* Warm Ochre/Sand: Conscious focus, active horizon, current day */
--accent-warm-subtle: rgba(212, 163, 115, 0.12);

/* Semantic States */
--state-progress: #2DD4BF;      /* Calm Teal/Sage: Positive momentum, healthy trajectory, completion */
--state-progress-subtle: rgba(45, 212, 191, 0.10);

--state-attention: #F59E0B;     /* Restrained Amber: Deadlines within 48h, needs conscious attention */
--state-attention-subtle: rgba(245, 158, 11, 0.10);

--state-critical: #EF4444;      /* Restrained Crimson: Genuinely overdue or destructive confirmation */
--state-critical-subtle: rgba(239, 68, 68, 0.10);
```

### Prohibitions
- **NO** neon purple accents.
- **NO** electric blue buttons everywhere.
- **NO** rainbow card borders or saturated status gradients.
- **NO** coloring every interactive element. Buttons and interactive surfaces rely primarily on high-contrast monochrome values, reserving color for true state changes.

---

## 8. Typography System

Typography carries the personality, calm authority, and editorial soul of LifeOS.

### Font Pairings
- **Display & Editorial Sans**: `Inter` (with optical sizing, calibrated weights, and negative letter spacing).
- **Tabular & Telemetry Mono**: `JetBrains Mono` (strictly for dates, time countdowns, percentages, and keyboard shortcuts with `font-variant-numeric: tabular-nums`).

### Editorial Hierarchy & Rhythm
| Role | Size | Weight | Tracking | Line Height | Application |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Hero Horizon** | `2.5rem` (40px) | `600` | `-0.035em` | `1.15` | Personal daily greeting, macro horizon statement |
| **Chapter / Section** | `1.5rem` (24px) | `500` | `-0.025em` | `1.25` | Major life domains ("Today's Focus", "Strategic Paths") |
| **Item Title** | `1.0625rem` (17px)| `500` | `-0.015em` | `1.4` | Focus action titles, goal titles |
| **Editorial Body** | `0.9375rem` (15px)| `400` | `-0.005em` | `1.6` | Contextual explanations, reasons, reflections |
| **Micro Caption** | `0.75rem` (12px) | `500` | `+0.04em` | `1.3` | Uppercase category anchors, subtle timestamps |
| **Metrics Mono** | `0.875rem` (14px) | `500` | `0` | `1.0` | `JetBrains Mono` for percentages, countdowns |

---

## 9. Surface & Depth System

LifeOS rejects wrapping every group of pixels in a heavy, bordered card. Depth is created through subtle luminance steps and spatial elevation.

```
Elevation Physics:
L0: Canvas (Infinite void floor)
     ↓ (0px elevation)
L1: Editorial Inset (Subtle indented grouping, 0.5px hair-rule)
     ↓ (+1px visual lift)
L2: Floating Interactive Anchor (Tactile focus item, smooth hover sheen)
     ↓ (+4px visual lift, soft contact shadow)
L3: Spatial Overlay (Modals, quick capture, context drawers with backdrop blur)
```

- **Top-Edge Ambient Light**: Surfaces feature a delicate 1px top highlight `rgba(255, 255, 255, 0.10)` that dissolves into `rgba(255, 255, 255, 0.03)` at the bottom edge.
- **Diffused Contact Shadows**: Multi-layered shadows (`0 8px 32px -4px rgba(0, 0, 0, 0.5)`) provide organic separation without harsh black outlines.

---

## 10. Composition & Spatial System

### "Stop Thinking in Card Grids"
The default layout of LifeOS is **not** a dashboard grid of uniform boxes. Instead, we use:
- **Open Sections**: Content groups separated by generous vertical rhythm (`32px`–`64px`) and delicate editorial rules, not enclosed containers.
- **Asymmetric Anchors**: A dominant focus area counterbalanced by a calm momentum stream.
- **Integrated Timelines & Paths**: Visual connections linking tasks directly into milestones without card boundaries.
- **Generous Whitespace**: Purposeful empty space that allows the mind to decompress and focus.

```
+-----------------------------------------------------------------------------------+
| ATMOSPHERIC HERO HORIZON                                                          |
| Personal greeting · Date · Calm Macro Horizon Statement · Whisper Momentum        |
+-----------------------------------------------------------------------------------+
| TODAY'S OPERATING CONTEXT                           | LIFE RHYTHM & CONTEXT       |
|                                                     |                             |
| [Current Position Anchor]                           | [Momentum & Consistency]    |
| Primary Action (amplified editorial scale)          | Weekly continuity dots      |
|                                                     | Rhythm without guilt        |
| Secondary Actions (progressive disclosure)          |                             |
| Action 2 · Action 3                                 | [Contextual Guidance]       |
|                                                     | Observation → Why → Suggest |
| [Active Journey Path]                               |                             |
| Destination (Goal)                                  | [Quick Horizon Check]       |
|   └── Checkpoint (Milestone)                        | Upcoming key inflection     |
|         └── Immediate Action                        |                             |
+-----------------------------------------------------------------------------------+
```

---

## 11. Information-Density & Progressive Disclosure Strategy

### The LifeOS 3-Second Rule
Within three seconds of opening LifeOS, the user must clearly perceive:
1. **Where am I?** (Current date, active life horizon, overall state of calm)
2. **What matters right now?** (The single highest-leverage focus action)
3. **What should I do next?** (One clear execution trigger)

### Progressive Disclosure Rules
- **No Tag Soup**: Never display Category + Priority + Deadline + Health + Confidence badges simultaneously on a single item.
- **Inline Hierarchy**: Category and Goal link are expressed via clean, whisper-weight typographic breadcrumbs (`Career / Deep Systems Study`).
- **Reveal on Demand**: Deep evaluation signals ("Why is this behind?", "Why is this recommended?") are disclosed via smooth contextual drawer or popover upon user click, not forced onto the primary view.

---

## 12. Navigation System

### Desktop Navigation: Quiet Sidebar Rail
- **Width**: `260px` expanded / `68px` collapsed.
- **Aesthetic**: Deep charcoal/black tone (`#08090D`) that flows seamlessly into the canvas edge.
- **Intent Grouping**: Small uppercase group headers (`HOME`, `FOCUS`, `PLAN`, `GROW`, `REFLECT`, `CAPTURE`) with generous spacing.
- **Active State Indicator**: Minimalist warm ochre vertical line (`2px`) with subtle surface tint (`rgba(212, 163, 115, 0.08)`). No neon pills.
- **Keyboard Affordance**: Quick Capture indicator (`Cmd+K`) embedded cleanly in the bottom utility area.

### Header: Whisper Utility Bar
- Height: `56px` sticky.
- Background: Translucent frosted canvas (`rgba(9, 10, 13, 0.8)` with backdrop blur).
- Elements: Clean typographic breadcrumb, global capture trigger, and unobtrusive theme toggle (`Dark` / `Light`).

### Mobile Navigation: The Execution Deck
- Priority: **Capture → Focus → Execute**.
- Height: `60px` bottom navigation bar with thumb-friendly hit targets (min `48x48px`).
- Quick Capture: Elevated central button with warm amber tactile press effect.
- Full Navigation Drawer: Smooth slide-up sheet presenting the complete 6-group taxonomy.

---

## 13. Dashboard Composition Strategy

The Dashboard answers: **"What matters in my life right now?"**

### Component Flow
1. **Hero Horizon Area**:
   - Editorial date and greeting (`"Good morning"`).
   - Macro Horizon Whisper: Current seasonal goal statement.
   - Micro Momentum Indicator: Subtle circular stroke indicating today's completion rhythm without loud gamified badges.
2. **Primary Operating Column**:
   - **Today's Focus Section**: 
     - Focus Item 1 (The Primary Anchor): Amplified visual presence, explicit connection to overarching goal, inline why-statement.
     - Focus Items 2 & 3: Secondary actions displayed cleanly below.
   - **Active Journey (Goal → Milestone → Task Path)**:
     - Spatial tree connecting the primary Goal destination directly to its next milestone checkpoint and underlying action.
3. **Secondary Context Column**:
   - **Contextual Recommendation**: Clean editorial block following `Observation → Why It Matters → Suggested Action`. Free of AI badges.
   - **Habit Rhythm**: 7-day continuity pattern focusing on consistency and recovery rather than guilt-inducing streak loss.

---

## 14. LifeOS Visual Metaphors

LifeOS visually manifests the living journey of personal growth:
```
GOAL (Destination)
  ↓
MILESTONE (Checkpoint)
  ↓
TASK (Action along the path)
  ↓
ACTION (Execution)
  ↓
PROGRESS (Forward movement)
  ↓
REFLECTION (Calm perspective)
  ↓
RECOMMENDATION (Intelligent guidance)
  ↓
NEXT FOCUS (Current position)
```

- **Not Gamified, Not a Toy**: Visualized through elegant cartographic lines, delicate node markers, and smooth spatial expansion.
- **Forward Momentum**: Checking off a task visually transmits energy into the milestone node, showing progress along the journey line.

---

## 15. Goals & Life Map Visual Strategy

- **Goals as Destinations**: Goals are given visual majesty through subtle atmospheric framing, target horizons, and clear progress velocity.
- **Milestones as Checkpoints**: Milestones are visually laid out as waypoints along a journey track.
- **Spatial Life Map Prototype**: An interactive view where expanding a Goal seamlessly cascades its milestone checkpoints and reveals the tasks that feed them, making the macro-to-micro connection tangible.

---

## 16. Today's Focus Visual Strategy

- **Operating Context**: Clear visual prominence for what deserves attention *today*.
- **The Anchor Item**: The top priority task is visually differentiated through warm typographic focus and subtle surface lift.
- **Completion Physics**:
  - Checking a task triggers an immediate, satisfying tactile interaction:
    1. Checkbox smoothly checks with custom spring curve.
    2. Subtle strike-through and text dimming.
    3. Milestone progress bar smoothly animates forward.
    4. Momentum score updates in real-time.

---

## 17. Motion & Interaction System

Motion is an architectural material in LifeOS, establishing spatial continuity and physical reality.

### Timing & Transition Curves
```css
--duration-tactile: 150ms;   /* Checkbox, button press, toggle */
--duration-context: 240ms;   /* Dropdown, popover, inline reveal */
--duration-spatial: 380ms;   /* Panel morph, drawer slide, milestone tree cascade */

--ease-lifeos: cubic-bezier(0.16, 1, 0.3, 1); /* Rapid start, smooth organic glide */
```

### Motion Patterns
- **Spatial Expansion**: Expanding a goal expands downward from the card itself, preserving user orientation.
- **Contextual Sheet Emergence**: Quick Capture and drawers slide smoothly with spring deceleration.
- **Reduced Motion Support**: Strict adherence to `@media (prefers-reduced-motion: reduce)` zeroing out all transitions instantly.

---

## 18. Responsive & Mobile Strategy

LifeOS on mobile is a first-class personal cockpit, not a scaled-down desktop screen.

### Breakpoint Matrix
- **Mobile (`< 768px`)**: Single-column vertical flow; bottom thumb navigation bar; full-screen slide sheets for Quick Capture and navigation.
- **Tablet (`768px – 1024px`)**: Collapsed icon-rail sidebar; single or 60/40 adapted layout; touch-optimized spacing.
- **Desktop (`1024px – 1440px`)**: Full expanded sidebar; asymmetric editorial dashboard layout.
- **Ultra-Wide (`> 1440px`)**: Content bounded to `1320px` max-width with balanced cinematic margins.

---

## 19. Component System Architecture

Components are built as focused, composable primitives in `src/components/ui/` consuming semantic tokens:

```
frontend/src/components/
├── navigation/
│   ├── Header.jsx & Header.css
│   ├── Sidebar.jsx & Sidebar.css
│   └── MobileNav.jsx & MobileNav.css
└── ui/
    ├── Badge.jsx & Badge.css             # Restrained status indicators
    ├── Button.jsx & Button.css           # High-contrast tactile buttons
    ├── Checkbox.jsx & Checkbox.css       # Custom smooth SVG checkbox
    ├── Drawer.jsx & Drawer.css           # Slide-in contextual sheet
    ├── EmptyState.jsx & EmptyState.css   # Atmospheric calm empty state
    ├── Input.jsx & Input.css             # Minimalist text input
    ├── LoadingState.jsx & LoadingState.css # Whisper skeleton pulse
    ├── Modal.jsx & Modal.css             # Focused dialog overlay
    ├── ProgressBar.jsx & ProgressBar.css # Linear & ring progress indicators
    ├── QuickCaptureModal.jsx & .css     # Command-K quick capture
    ├── Select.jsx & Select.css           # Custom styled dropdown
    ├── Surface.jsx & Surface.css         # Composable open/card surface primitive
    ├── Tabs.jsx & Tabs.css               # Clean underline/pill tabs
    ├── Textarea.jsx & Textarea.css       # Auto-expanding text area
    ├── ThemeToggle.jsx & ThemeToggle.css # Seamless dark/light toggle
    ├── Toggle.jsx & Toggle.css           # Switch toggle
    └── Tooltip.jsx & Tooltip.css         # Accessible hover tooltip
```

---

## 20. Accessibility (a11y) Standards

- **Contrast Ratios**: Strictly WCAG AA compliant across both Deep Black and Light themes (minimum 4.5:1 for body text, 3:1 for large text/icons).
- **Keyboard Navigation**:
  - Full tab order across all interactive elements.
  - Visible, non-intrusive focus rings (`--focus-ring`: `rgba(212, 163, 115, 0.6)`).
  - Focus trapping and Escape key handling in all modals, drawers, and popovers.
  - Global `Cmd/Ctrl + K` and single-key `C` capture triggers.
- **Semantic HTML & ARIA**: Use of `<main>`, `<nav>`, `<header>`, `<section>`, `aria-expanded`, `aria-label`, and `aria-live`.

---

## 21. External Reference Usage Guidelines

We draw specific technical and interaction inspiration from vetted references without copying their aesthetic:
- **60fps.design**: Inspires the 240–380ms organic spring curves for panel sliding and milestone expansion.
- **21st.dev**: Informs clean, accessible React component APIs (composable subcomponents).
- **glass3d.dev**: Informs delicate top-edge ambient light falloff (`0.5px`–`1px`), strictly avoiding heavy neon glassmorphism.

---

## 22. AI-Slop Prevention Rules

To safeguard LifeOS's visual integrity, the following patterns are **permanently prohibited**:
1. **NO purple gradient buttons or text.**
2. **NO neon glowing card borders.**
3. **NO floating gradient blobs or blurry colorful background spheres.**
4. **NO repeating identical rounded boxes in a grid.**
5. **NO "AI-Powered", "Smart Engine", or futuristic hype badges.**
6. **NO decorative charts or graphs that lack concrete actionable meaning.**
7. **NO wall of cards.**

---

## 23. Phase 3 Implementation Sequence

Execution will follow this disciplined 7-step sequence:

### Step 1: Token Engine & Dual-Theme Refactoring
- Rewrite `src/design-system/tokens/tokens.css` with Deep Black default tokens and `[data-theme="light"]` semantic tokens.
- Add warm ochre accent tokens, calm teal momentum tokens, and surface elevation tiers as initial tokens.
- Remove all neon indigo and purple variables.

### Step 2: Global Canvas & Typography Foundation
- Update `index.html` with Google Fonts preconnect for `Inter` (weights 300–700) and `JetBrains Mono` (weights 400–600).
- Update `src/index.css` with base viewport canvas physics, custom scrollbars, selection styling, and typographic baseline.

### Step 3: Component Primitives Refactoring
- Update `Button`, `Checkbox`, `Badge`, `ProgressBar`, `Input`, `Modal`, `Drawer`, `Tabs` to consume new semantic tokens.
- Strip card-grid assumptions; create `Surface.jsx` for flexible open vs. elevated container layouts.
- Create `ThemeToggle.jsx` primitive for seamless dark/light switching.

### Step 4: Navigation Shell Overhaul
- Refactor `Sidebar.jsx`, `Header.jsx`, and `MobileNav.jsx` with the quiet charcoal aesthetic, refined active indicators, and theme toggle.
- Verify smooth collapse transitions (260px → 68px).

### Step 5: Dashboard Re-Architecting
- Complete rewrite of `DashboardPage.jsx` and `DashboardPage.css` around the open editorial layout.
- Implement Atmospheric Hero Horizon, Today's Focus operating anchor, Goal → Milestone → Task spatial journey, Habit rhythm dots, and Contextual recommendation as **pure frontend prototypes**.

### Step 6: Signature Interactions & Quick Capture
- Refactor `QuickCaptureModal.jsx` to match the new visual language.
- Wire task completion momentum transition (checking focus item updates linked milestone progress and daily momentum gauge in mock state).

### Step 7: Dual-Theme & Responsive Visual QA
- Validate all breakpoints: Desktop (1440px), Laptop (1200px), Tablet (768px), Mobile (390px).
- Evaluate and fine-tune initial color tokens in the live interface for optimal contrast and warmth.
- Verify instant theme switching with zero contrast regressions.
- Verify Phase 1 testbed remains intact at `/dev/foundation`.

---

## 24. Verification and Visual QA Process

### Automated Verification
1. **Frontend Production Build**: `npm run build` in `frontend/` (zero warnings, clean bundle).
2. **Backend Regression Verification**: `.\mvnw.cmd test` in `backend/` (100% pass on security and foundation tests).

### Visual & Experiential Quality Gates
1. **The "No SaaS" Test**: Does the page feel like an editorial operating environment rather than a SaaS dashboard when branding is obscured?
2. **Contrast & Hierarchy**: Is the most important action for today instantly obvious within 3 seconds?
3. **Calm Atmosphere**: Does the dark theme feel restful and glare-free?
4. **Theme Fidelity**: Does the light theme preserve 100% of the structural hierarchy, typography, and elegance?
5. **Mobile Fluidity**: Does mobile navigation operate smoothly without horizontal overflow, text clipping, or tiny touch targets?

---

## 25. Definition of Done (DoD) for Phase 3

Phase 3 will be considered complete when:
- [ ] The revised design tokens support Deep Black (default) and Light themes seamlessly, with initial values evaluated and tuned in the live UI.
- [ ] The Dashboard is fully transformed into an open, editorial, cinematic layout free of generic SaaS card grids and purple accents.
- [ ] Today's Focus provides clear operational priority with interactive task completion momentum.
- [ ] Goal → Milestone → Task spatial hierarchy is visible and interactively explorable.
- [ ] Contextual Recommendation follows the clean `Observation → Why → Action` pattern without AI buzzwords.
- [ ] Habit Consistency displays calm rhythm without punitive streaks.
- [ ] Quick Capture (`Cmd+K`) functions smoothly with keyboard navigation.
- [ ] **All Dashboard interactions operate strictly as frontend visual and interaction prototypes using centralized mock data, with zero real backend logic, database changes, or CRUD APIs implemented.**
- [ ] Responsive layouts are tested and fluid across mobile, tablet, and desktop.
- [ ] Frontend builds cleanly with zero errors; backend tests continue to pass 100%.
- [ ] Diagnostic route `/dev/foundation` remains fully functional.

---

## 26. Final Alignment & Design Details

1. **Theme Switcher Placement**: Integrated into the Header utility bar (right side next to avatar) and mobile drawer footer.
2. **Goal Visual Exploration**: In Phase 3, the Goal → Milestone → Task journey is demonstrated in the primary dashboard column. (Full standalone Life Map canvas view is slated for the dedicated Goals/Life Map phase).
3. **Realistic Demonstration Data (Persona-Agnostic)**: Mock data in `src/mocks/dashboardMockData.js` will feature balanced, grounded life domains (career growth, technical or craft mastery, personal projects, fitness and daily routines) to showcase the LifeOS relational hierarchy directly, avoiding any fictionalized persona.

---
**Status:** Plan finalized with all three requested corrections applied. Execution halted. Waiting for final approval.
