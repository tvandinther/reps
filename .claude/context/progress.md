# Progress

Current state of the build. Update at the end of every session.

---

## Status: First build succeeded — APK compiles cleanly from WSL2

---

## Done

- [x] Product vision defined (`PRODUCT_VISION.md`)
- [x] Architecture defined (`ARCHITECTURE.md`)
- [x] Data model finalised — three tables: `units`, `exercises`, `sets`
- [x] Unit model agreed — two axes per exercise (volume + resistance)
- [x] Session clustering approach agreed — 90-minute gap threshold, derived at query time
- [x] Navigation structure agreed — two tabs: Exercises (with stack to SetLogging) + History
- [x] MVP scope locked
- [x] `CLAUDE.md` and project memory structure established
- [x] Design exploration in progress — directions 3 and 4 under consideration
- [x] **Phase 1:** Project scaffold — Gradle Kotlin DSL, devbox.json (JDK17 + Gradle), all dependencies
- [x] **Phase 2:** Database layer — Room entities (units/exercises/sets), UnitDao, ExerciseDao, SetDao, unit seeding, SessionAssembler, unit tests
- [x] **Phase 3:** Exercises screen — fuzzy search, Add sheet with unit pickers, last-set summary rows
- [x] **Phase 4:** Set logging screen — current session, ghost sets, swipe-to-delete, Add Set bottom sheet, RPE, auto-save
- [x] **Phase 5:** History screen — session assembly from LAG() query, collapsible sessions
- [x] **Phase 6:** Koin wiring, dark theme, navigation, StateFlow throughout
- [x] **Phase 7:** First successful build from WSL2 — fixed SDK path, launcher icons, Room SQL parser issues

## In Progress

- [ ] Design direction — choosing between Direction 3 and Direction 4

## Up Next

- [ ] Install APK on device and smoke test all screens and flows
- [ ] Design system pass once aesthetic direction is confirmed

## Backlog (post-MVP)

- [ ] Cloud backup (user-owned Google Drive)
- [ ] Charting per exercise
- [ ] Rest timers (local notifications)
- [ ] AI features (user-supplied Anthropic API key)
- [ ] Templates and programming
- [ ] Settings screen (gap threshold, default units)
- [ ] Google Fit integration
