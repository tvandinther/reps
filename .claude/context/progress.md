# Progress

Current state of the build. Update at the end of every session.

---

## Status: Pre-build — design and architecture phase

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

## In Progress

- [ ] Design direction — choosing between Direction 3 (Soviet Sports Science) and Direction 4 (Editorial/Typographic), or a hybrid
- [ ] Design system document — typeface, color tokens, spacing, component primitives

## Up Next

- [ ] Project scaffold — Gradle setup, package structure, dependency declarations
- [ ] Room schema — entities, DAOs, database class, migrations
- [ ] Unit seeding — insert default units on first launch
- [ ] Exercises screen — list, fuzzy search, add flow
- [ ] Set logging screen — current session, ghost sets, RPE, auto-save
- [ ] History screen — clustered sessions, read-only

## Backlog (post-MVP)

- [ ] Cloud backup (user-owned Google Drive)
- [ ] Charting per exercise
- [ ] Rest timers (local notifications)
- [ ] AI features (user-supplied Anthropic API key)
- [ ] Templates and programming
- [ ] Settings screen (gap threshold, default units)
- [ ] Google Fit integration
