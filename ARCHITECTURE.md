# Architecture

## Platform

- **Target:** Android only
- **Distribution:** Sideload APK or Google Play Store ($25 one-time)
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Minimum API:** 30 (required for SQLite window functions used in session clustering)

## Principles

- **Local-first.** All data lives on device in SQLite. No backend, no accounts, no cost to run.
- **Flat logbook.** A `set` is the only stored unit of work. Sessions, groupings, and summaries are derived at query time — never stored.
- **No free lunch.** Any feature requiring a backend (AI, sync) uses the user's own API keys or cloud credentials.

## Tech Stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose |
| Database | Room (SQLite ORM) |
| Async | Kotlin Coroutines + Flow |
| State | ViewModel + StateFlow |
| Dependency Injection | Koin |
| Navigation | Compose Navigation |
| Build | Gradle (Kotlin DSL) |

## Data Model

Three tables. Nothing else.

```sql
CREATE TABLE units (
  id        INTEGER PRIMARY KEY,
  label     TEXT NOT NULL,        -- "kg", "seconds", "meters", etc.
  type      TEXT NOT NULL,        -- "volume" | "resistance"
  is_default INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE exercises (
  id                 INTEGER PRIMARY KEY,
  name               TEXT NOT NULL,
  volume_unit_id     INTEGER NOT NULL REFERENCES units(id),
  resistance_unit_id INTEGER NOT NULL REFERENCES units(id),
  note               TEXT,               -- optional description of the exercise
                                         -- e.g. cues, setup instructions, variations
  created_at         INTEGER NOT NULL,   -- unix ms
  last_logged_at     INTEGER             -- unix ms, null until first set
);

CREATE TABLE sets (
  id               INTEGER PRIMARY KEY,
  exercise_id      INTEGER NOT NULL REFERENCES exercises(id),
  volume_value     REAL NOT NULL,        -- reps, seconds, meters, etc.
  resistance_value REAL,                -- null for bodyweight / none
  rpe              INTEGER,             -- 1–10, optional
  note             TEXT,               -- optional per-set annotation
                                        -- e.g. "failed rep 4", "shoulder twinge",
                                        --      "paused reps", "wider grip"
  logged_at        INTEGER NOT NULL     -- unix ms
);
```

### Seeded Units

**Volume** (how much / how long):
`reps`, `seconds`, `minutes`, `meters`, `km`, `miles`, `calories`

**Resistance** (against what load):
`kg`, `lb`, `bodyweight` (no value), `level` (machine), `band` (1–5 scale), `none`

Users can add custom units of either type.

## Session Clustering

Sessions are never stored. They are derived at query time by clustering sets separated by less than a configurable gap threshold.

**Default threshold:** 90 minutes

**Algorithm:**
```sql
-- Sets are ordered by logged_at.
-- A new session boundary is declared when the gap between
-- consecutive sets (across all exercises) exceeds the threshold.
SELECT *,
  CASE
    WHEN (logged_at - LAG(logged_at) OVER (ORDER BY logged_at)) > :gapMs
    THEN 1 ELSE 0
  END AS session_break
FROM sets
ORDER BY logged_at DESC
```

This is assembled in the DAO layer into `Session` objects (a runtime-only data class) before reaching the ViewModel. The threshold will be user-configurable in a future settings screen.

## Display Modes

Driven by the unit type combination on the exercise:

| Volume Unit | Resistance Unit | Display |
|---|---|---|
| reps | kg / lb / level / band | `5 reps × 225 lb` |
| reps | bodyweight | `12 reps (bodyweight)` |
| reps | none | `12 reps` |
| seconds / minutes | any | `60 seconds × bodyweight` |
| meters / km / miles | any | `500 meters` |

## Navigation Structure

```
BottomNav
├── Tab 1: Exercises (start destination)
│   └── Stack: ExercisesScreen → SetLoggingScreen
└── Tab 2: History
```

No explicit workout or session concept in navigation. The user selects an exercise, logs sets, and navigates back. The session is inferred from timing.

## Future Considerations

These are out of scope for MVP but the architecture should not preclude them:

- **Cloud backup:** Export a single JSON snapshot to iCloud / Google Drive using the user's own credentials. No proprietary sync service.
- **AI features:** Natural language logging, progression suggestions. Requires user-supplied Anthropic API key.
- **Charting:** Per-exercise volume and load trends. Derived entirely from the `sets` table.
- **Templates & programming:** Saved exercise sequences. Would require one additional table; no changes to core schema.
- **Rest timers:** Local notifications only, no backend.
- **HealthKit / Google Fit:** Write workout summaries. Android-only scope makes this straightforward.
- **Settings:** Gap threshold for session clustering, default units, weight unit preference.
