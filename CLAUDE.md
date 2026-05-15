# CLAUDE.md

This file is read by Claude Code at the start of every session. It orients any contributor — human or AI — to the project without requiring prior context.

---

## What This Project Is

A native Android workout logging app. Fast, friction-free, ad-hoc. The user opens the app and logs. No program setup, no session declaration, no library to pre-configure. A flat logbook with smart UX layered on top.

Read `PRODUCT_VISION.md` and `ARCHITECTURE.md` before making any changes. They are the source of truth for why decisions were made. Do not work around them — update them if something changes.

---

## Project Memory

Long-running context that would otherwise be lost between sessions lives in:

```
.claude/
  context/
    decisions.md       — architectural and design decisions with rationale
    open-questions.md  — unresolved questions and tradeoffs being considered
    progress.md        — what has been built, what is in progress, what is next
    design.md          — design system, typeface choices, visual direction
  sessions/
    YYYY-MM-DD.md      — notable session logs (created when something significant happens)
```

### Rules for maintaining project memory

- **Read `.claude/context/` at the start of every session.** Before writing any code or making any decision, read all four context files. They are short by design.
- **Update context files when anything significant changes.** A decision made, a question resolved, a direction chosen — write it down before ending the session. Future sessions depend on it.
- **Write session logs sparingly.** Only create a session file in `.claude/sessions/` when something non-obvious happened: a significant refactor, a rejected direction, a hard-won fix. Not every session warrants one.
- **Keep entries concise.** These files are scanned, not read cover to cover. Bullet points over prose. Dates on every entry.

---

## Core Principles (Do Not Violate)

These are non-negotiable. If a feature request conflicts with them, push back or raise it in `open-questions.md`.

1. **Flat logbook.** `sets` is the only stored unit of work. Sessions are derived at query time via gap clustering — never stored, never declared by the user.
2. **Local-first.** No backend, no accounts, no cost to run. Data lives on device in SQLite via Room.
3. **No free lunch.** Features requiring a backend (AI, sync) must use the user's own credentials or API keys.
4. **Minimal friction.** The core flow is: open app → find or create exercise → log set. Every added step is a regression.
5. **Auto-save.** Sets are saved on entry. There is no save button, no finish workout button, no confirmation step.

---

## Architecture at a Glance

- **Platform:** Android only, Kotlin, Jetpack Compose, minimum API 30
- **Stack:** Room · Coroutines + Flow · ViewModel + StateFlow · Koin · Compose Navigation
- **Schema:** Three tables — `units`, `exercises`, `sets`. Nothing else.
- **Sessions:** Derived by clustering sets with gaps > 90 minutes (SQLite window functions)

Full detail in `ARCHITECTURE.md`.

---

## MVP Scope

The MVP is deliberately narrow. Do not add features outside this list without updating `PRODUCT_VISION.md` and recording the decision in `.claude/context/decisions.md`.

**In scope:**
- Exercises screen with fuzzy search and inline add
- Set logging screen with last-session ghost sets
- RPE per set (optional, never blocking)
- Per-set and per-exercise notes
- History screen (derived sessions, read-only)
- Custom units (volume and resistance, both axes)

**Explicitly out of scope for MVP:**
- Timers
- Charting or analytics
- Templates or programs
- Cloud backup / sync
- AI features
- Social features
- Accounts

---

## Key UX Decisions

- **Search bar is always autofocused** on the Exercises screen
- **"Add [query]"** appears inline when no match exists — one tap creates and navigates to the exercise
- **Exercise list is ordered by `last_logged_at` DESC** — self-organising, no manual sorting
- **Units are per-exercise** — two axes: volume unit (reps/seconds/meters/etc.) and resistance unit (kg/lb/bodyweight/etc.)
- **Ghost sets** from the previous session are shown muted below the current session on the logging screen
- **No back-confirmation, no save prompt** — the user logs and leaves

---

## Data Model (Quick Reference)

```sql
units      (id, label, type, is_default)
           type: "volume" | "resistance"

exercises  (id, name, volume_unit_id, resistance_unit_id, note, created_at, last_logged_at)

sets       (id, exercise_id, volume_value, resistance_value, rpe, note, logged_at)
```

Seeded volume units: `reps`, `seconds`, `minutes`, `meters`, `km`, `miles`, `calories`
Seeded resistance units: `kg`, `lb`, `bodyweight`, `level`, `band`, `none`

---

## Starting a New Session

1. Read `PRODUCT_VISION.md`
2. Read `ARCHITECTURE.md`
3. Read `.claude/context/decisions.md`
4. Read `.claude/context/progress.md`
5. Read `.claude/context/open-questions.md`
6. Check `.claude/context/design.md` if touching UI

Then ask: *does what I'm about to do align with the principles above?* If unsure, write the question into `open-questions.md` before proceeding.

---

## Ending a Session

Before closing:

- Update `progress.md` with what changed
- Record any decisions made in `decisions.md`
- Close out or update any questions in `open-questions.md`
- Create a session log in `sessions/YYYY-MM-DD.md` only if something non-obvious happened
