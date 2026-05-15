# Design

Visual direction, typeface choices, color tokens, and component conventions. Update when design decisions are made.

---

## Status: Direction under active consideration

Two directions are being evaluated. Do not implement UI until one is chosen and this file is updated with the final system.

---

## Direction 3 — Soviet Sports Science

**Character:** Bold, constructivist, poster energy. Shouts. Feels like competition.  
**Primary typeface:** Barlow Condensed — geometric, muscular, all-caps labels and UI chrome  
**Data typeface:** Spectral — sharp editorial serif for numbers  
**Accent:** `#cc0000` — hard red, functional not decorative  
**Background:** `#0c0c0c`  
**Active state:** red text and borders  
**Add action:** full-width red bar  

## Direction 4 — Editorial / Typographic

**Character:** Quietly authoritative, newspaper precision. Does not shout. Feels like a well-kept training journal.  
**Primary typeface:** Playfair Display — high-contrast editorial serif for headings and labels  
**Data typeface:** IBM Plex Mono — monospace for all numbers (reps, weight, RPE)  
**Accent:** `#e8c170` — warm amber, reads as ink rather than danger  
**Background:** `#060606`  
**Active state:** amber underlines and values  
**Italic:** used for secondary labels and ghost-state copy  

---

## Shared Principles (apply to either direction)

- **Dark mode only.** Gyms are dim. Never implement a light theme.
- **High contrast.** Primary text must be clearly legible at arm's length, mid-set.
- **Numbers dominate.** The last set value (weight, reps) should be the most visually prominent element in any list row.
- **Large tap targets.** Minimum 48dp for anything interactive. Mid-workout hands are sweaty.
- **No decorative icons.** Use typographic markers (arrows, dashes, counters) rather than icon libraries wherever possible.
- **Ghost sets** (previous session) rendered at reduced opacity — approximately 30–40% — never hidden.

---

## To be completed once direction is chosen

- Color token definitions (background, surface, border, text primary/secondary/ghost, accent)
- Typography scale (display, heading, body, label, mono-data)
- Spacing scale
- Component specifications (set row, exercise row, search bar, add hint, tab bar)
