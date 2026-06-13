# Decisions

Architectural and design decisions with rationale. Most recent first.

---

## Android-only, native Kotlin

**Date:** project start  
**Decision:** Target Android only. No cross-platform framework.  
**Rationale:** No intention to publish on iOS (App Store friction, cost). Android supports sideloading trivially and Play Store is a $25 one-time fee. Native Kotlin + Compose gives best performance and full platform access with no abstraction overhead. Contributor background (C#, Go) maps well to Kotlin.

---

## No stored sessions

**Date:** project start  
**Decision:** Sessions are never stored. They are derived at query time by clustering sets with a gap threshold of 90 minutes.  
**Rationale:** Storing sessions requires the user to declare them — a "start workout" / "end workout" flow that adds friction and can be forgotten. The clustering approach is invisible to the user and requires no action. The threshold will be user-configurable in a future settings screen.

---

## Two-axis unit model (volume + resistance)

**Date:** project start  
**Decision:** Each exercise carries two independent unit axes: a volume unit (reps, seconds, meters, etc.) and a resistance unit (kg, lb, bodyweight, level, band, none).  
**Rationale:** A single unit field cannot represent exercises cleanly. A plank is `60 seconds × bodyweight`. A rowing machine is `500 meters × level 8`. Separating the axes handles every exercise type without special cases. Distance (meters, km) is a volume unit — the exercise was performed for x distance. Resistance level on a machine is a resistance unit — it's a number that means something relative to that machine.

---

## Sets as the sole stored unit

**Date:** project start  
**Decision:** `sets` is the only table that stores workout data. No sessions table, no exercise_log join table.  
**Rationale:** The app is a flat logbook. Any grouping (by session, by exercise within a session, by week) is a view concern, not a storage concern. Keeping the schema flat makes it easier to query, export, and reason about. Room's `@Query` with window functions handles the clustering without additional tables.

---

## Session gap computed in Kotlin, not SQL window functions

**Date:** 2026-05-16  
**Decision:** The session break computation was moved from a SQL `LAG()` window function to Kotlin in `SessionAssembler`.  
**Rationale:** Room's KSP SQL parser does not support window functions in `@Query` annotations. Since the sets are already loaded as a `Flow<List<SetEntity>>` for reactivity, computing gaps in Kotlin is equivalent and avoids `@RawQuery` complexity. The `SetWithBreak` POJO was removed. The architecture principle (sessions derived at query time, never stored) is preserved — the derivation just happens one layer up.

---

## Minimum API 30

**Date:** project start  
**Decision:** Minimum supported Android API is 30 (Android 11).  
**Rationale:** The session clustering query uses `LAG()` window functions, which require SQLite 3.25+. This ships with API 30. Going lower would require a bundled SQLite or a different clustering approach — not worth the complexity.

---

## Exercise progress chart — Canvas dot plot, no charting library

**Date:** 2026-06-13  
**Decision:** Chart is implemented as a custom Compose Canvas draw call with no third-party charting library.  
**Rationale:** The design is intentionally minimal and brutalist. A library would impose visual conventions that conflict with the app's aesthetic. Canvas gives full control over dot placement, color, and size gradients with very little code. The chart is a dot/scatter plot only — no lines, no bars, no tooltips.

---

## Chart color gradient — grey-to-orange, saturation reserved for newest sets

**Date:** 2026-06-13  
**Decision:** Oldest ~50% of sets render as dark-to-medium grey, next ~35% as medium-to-light grey, most recent ~15% transition to full ColorSignal orange. Dot size increases only in the last ~12% of sets.  
**Rationale:** Keeps visual noise low for historical data while making progress immediately legible. The orange "hot zone" at the right/top of the chart is the user's current performance level.
