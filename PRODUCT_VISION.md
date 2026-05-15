# Product Vision

## What This App Is

A fast, structured logbook for repetitive workouts — weightlifting, functional training, bodyweight work, cardio machines — performed under controlled conditions where detailed logging enables trend identification over time.

It is not a social app. It is not a coaching platform. It is a personal instrument.

## The Core Differentiator

**Minimal friction between walking into the gym and logging your first set.**

Most workout apps front-load friction: pick a program, start a workout, select from a library, confirm the exercise. This app inverts that flow.

The primary screen is a single list of every exercise the user has ever logged, ordered by most recently used. A search bar is always focused and ready. If the exercise exists, one tap starts logging. If it does not exist, a single additional tap creates it and starts logging immediately.

There is no concept of "starting a workout." You open the app and log. Sessions are inferred automatically from timing gaps between sets — they are never declared by the user.

## Purpose

- Log sets as fast as possible, even mid-exercise with sweaty hands
- Provide just enough context at the point of logging to make good decisions (what you did last time)
- Stay out of the way between sets
- Accumulate a reliable, structured history that can surface trends over time

## Who It Is For

Someone who trains consistently, runs a varied exercise selection, and wants a logbook that keeps up with them rather than constraining them to a predefined structure. They may do barbell work, kettlebells, machines, and bodyweight movements in the same session. They do not want to configure a program before they can log a set.

## UX Goals

### 1. Search is the primary interaction
The search bar on the Exercises screen is auto-focused on open. It serves two purposes simultaneously: filtering the existing exercise list with fuzzy matching, and acting as the input for creating a new exercise. These are not two separate flows — they are the same gesture.

### 2. Creation is a one-tap consequence of search
If a search query does not match any existing exercise, a single button appears inline: **"Add [query]"**. Tapping it opens a minimal creation sheet with the name pre-filled. The only required decision is the unit configuration (volume unit and resistance unit). On confirm, the exercise is created and the user is taken directly to logging it.

### 3. Last session is always visible at the point of logging
When logging sets for an exercise, the previous session's sets for that exercise are shown in a muted/ghost style directly below the current session. This allows the user to make informed decisions about load and volume without leaving the screen or consulting history.

### 4. Auto-save, no confirmation steps
Sets are saved the moment they are entered. There is no "finish workout" button, no "save session" step. The user logs and leaves. The app figures out the rest.

### 5. The list is self-organising
Exercises rise to the top of the list as they are used. A new user does not need to pre-populate a library. The list builds itself through use and naturally reflects the user's current training focus.

### 6. Units are per-exercise, not global
Each exercise carries its own volume unit (reps, seconds, meters, etc.) and resistance unit (kg, lb, bodyweight, machine level, etc.). A user can log kettlebell swings in kg, a plank in seconds, and a rowing machine in meters with resistance level — all in the same session — without changing any setting.

### 7. RPE is optional but always available
Rate of Perceived Exertion (1–10) can be recorded per set. It is never required and never blocks logging. Over time it enriches the history with subjective load data that weight alone cannot capture.

## What This App Deliberately Avoids (MVP)

- Predefined exercise libraries
- Mandatory program or routine setup
- "Start workout" / "End workout" flow
- Timers (rest or otherwise)
- Social or sharing features
- Accounts or cloud dependency
- AI features
- Charting or analytics screens

These may be added later. They are excluded from MVP not because they lack value, but because they are not the differentiator and their absence keeps the core experience fast and focused.
