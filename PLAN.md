# Sudoku Solver Webapp — Implementation Plan

## Context

We are building a web application that lets a user input an incomplete Sudoku puzzle and watch a backtracking algorithm solve it visually. The project directory `C:\Users\Azhar Uddin\Projects\Sudoku-Solver` is currently empty — this is a from-scratch build with no existing code or git repository.

The goals are:
- Minimal frontend (vanilla web tech, no build pipeline)
- Java backend running the actual solver (Spring Boot)
- A clear visual of the backtracking algorithm in action, with user-controllable speed
- A clean separation so the solver logic could later be reused outside the web layer

## Architecture overview

```
Browser (vanilla HTML/CSS/JS)
       │  POST /api/solve  { board: [[...]] }
       ▼
Spring Boot app (Java 21)
       │  SudokuValidator → SudokuSolver (backtracking, records every step)
       ▼
Response: { solved: true, finalBoard: [[...]], steps: [ {row,col,value,action}, ... ] }
       │
       ▼
Browser replays steps at user-chosen speed, highlighting current cell
```

Frontend and backend are served from the same Spring Boot process (frontend lives in `src/main/resources/static/`). This avoids CORS, simplifies deployment, and keeps everything one command to run.

## Tech stack decisions

| Layer | Choice | Reason |
|---|---|---|
| Frontend | Vanilla HTML + CSS + JavaScript (ES modules) | User wants minimal; no framework, no build step |
| Backend | Spring Boot 3.x with Java 21 | Confirmed by user; mainstream, easy REST endpoints |
| Build tool | Maven | Simpler than Gradle for a small project; one `pom.xml` |
| Step delivery | Single JSON response containing all steps | Confirmed by user; speed control lives client-side |
| Input method | Interactive 9x9 grid + paste box | Confirmed by user |
| Validation | Pre-solve check for rule violations in input | Confirmed by user |
| Visual feature | Highlight the cell being placed/backtracked | Confirmed by user |

## Frontend design

A single `index.html` with three sections:

1. **Input area**
   - A 9x9 grid of `<input>` cells with thick borders separating 3x3 boxes (CSS grid + nth-child borders).
   - A textarea labeled "Paste puzzle" accepting an 81-character string (`0` or `.` for blanks). A button "Load from text" populates the grid.
   - A "Solve" button.

2. **Controls**
   - Radio buttons or a dropdown for speed: `Slow (300ms/step)`, `Regular (100ms/step)`, `Fast (30ms/step)`, `Very Fast (5ms/step)`, `No visual — show result only`.
   - Pause / Resume / Reset buttons (reset clears state, not the input).

3. **Status area**
   - A small text region for messages: "Invalid puzzle: duplicate 7 in row 3", "Solving…", "Solved in N steps", "Unsolvable".

### Animation logic
- After receiving the steps array, a JS module iterates through it using `setTimeout` recursion with the delay matched to the chosen speed.
- For each step: apply the value to the cell, add a CSS class `.cell-active` to highlight, then on the next step remove the class from the previous cell. Backtrack steps use a different class `.cell-backtrack` (e.g., red flash) so the user can see when the algorithm undoes a guess.
- "No visual" mode skips the animation loop entirely and just paints `finalBoard` at once.

### File layout (frontend)
```
src/main/resources/static/
  index.html
  styles.css
  app.js          # entry point, wires up DOM events
  grid.js         # grid render + read/write helpers
  animator.js     # step replay engine with speed + pause
  api.js          # fetch wrapper for POST /api/solve
```

## Backend design

### Maven coordinates
- `org.springframework.boot:spring-boot-starter-web` for REST
- Java 21
- Single module, package root `com.azhar.sudoku`

### Packages and classes
```
com.azhar.sudoku
├── SudokuApplication.java        # @SpringBootApplication entry point
├── api
│   ├── SolveController.java      # POST /api/solve
│   ├── SolveRequest.java         # { board: int[9][9] }  (0 = blank)
│   └── SolveResponse.java        # { solved, finalBoard, steps, stepCount, elapsedMs }
├── solver
│   ├── SudokuSolver.java         # backtracking, records steps
│   ├── SudokuValidator.java      # pre-solve rule check
│   └── Step.java                 # record (int row, int col, int value, Action action)
└── exception
    └── GlobalExceptionHandler.java   # @ControllerAdvice for clean error JSON
```

### Solver algorithm
- Classic recursive backtracking.
- Find next empty cell (row-major order is fine; "minimum remaining values" heuristic is unnecessary for this scope).
- For digits 1–9, check row/column/3x3-box constraints. If valid, place, record `Step(row, col, value, PLACE)`, recurse.
- On dead end, record `Step(row, col, 0, BACKTRACK)` and continue.
- `Action` enum: `PLACE`, `BACKTRACK`.
- Solver returns the steps list plus the final board (or null if unsolvable).

### Validator
- Iterates the input board. For each filled cell, checks no duplicate in its row, column, or 3x3 box.
- Returns either `Valid` or `Invalid(reason)` (sealed interface or simple result class with a message field).

### REST contract
- `POST /api/solve`
  - Request: `{ "board": [[5,3,0,...], [6,0,0,...], ...] }` — 9x9 of ints, 0 = blank
  - Success: `{ "solved": true, "finalBoard": [[...]], "steps": [{"row":0,"col":2,"value":4,"action":"PLACE"}, ...], "stepCount": 1247, "elapsedMs": 38 }`
  - Validation failure: HTTP 400, `{ "solved": false, "error": "Duplicate 7 in row 3" }`
  - Unsolvable: HTTP 200, `{ "solved": false, "error": "Puzzle has no solution", "steps": [...] }` (we still return steps so the user can watch the exhaustive search)

## Project structure (full)

```
Sudoku-Solver/
├── PLAN.md                       # This file
├── PROGRESS_TRACKER.md           # Granular checklist driving git commits
├── README.md                     # How to run
├── .gitignore                    # Maven target/, IDE files, OS files
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/azhar/sudoku/
    │   │   ├── SudokuApplication.java
    │   │   ├── api/
    │   │   ├── solver/
    │   │   └── exception/
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           ├── index.html
    │           ├── styles.css
    │           ├── app.js
    │           ├── grid.js
    │           ├── animator.js
    │           └── api.js
    └── test/
        └── java/com/azhar/sudoku/solver/
            ├── SudokuSolverTest.java       # JUnit 5: solves known puzzles, detects unsolvable
            └── SudokuValidatorTest.java    # detects duplicates per rule
```

## Execution phases (high level — `PROGRESS_TRACKER.md` holds the granular checklist)

1. **Project bootstrap** — git init, `.gitignore`, `pom.xml`, Spring Boot skeleton, `SudokuApplication`, verify `mvn spring-boot:run` starts.
2. **Solver core** — `Step`, `Action`, `SudokuSolver`, `SudokuValidator`, with unit tests for known puzzles (easy, hard, invalid, unsolvable).
3. **REST API** — `SolveController`, request/response DTOs, exception handler, smoke-test via curl.
4. **Frontend grid + paste** — HTML grid, CSS, paste-to-grid loader, no solving yet.
5. **Frontend wiring** — `api.js`, click Solve → POST → render `finalBoard` instantly (no animation yet).
6. **Animator** — replay engine with speed selector, pause/resume, cell highlighting, backtrack flash.
7. **No-visual mode + polish** — skip animation toggle, step counter display, elapsed time, status messages.
8. **README + final verification** — how to build/run, screenshots optional.

## Verification plan

End-to-end checks that must pass before calling the project "done":

1. **Backend unit tests**: `mvn test` — all green. Solver test covers an easy puzzle, the hardest known 17-clue puzzle, and an invalid puzzle.
2. **Backend smoke test**: `curl -X POST http://localhost:8080/api/solve -H "Content-Type: application/json" -d '{"board":[[5,3,0,...]]}'` returns `solved:true` with a valid solution (each row/col/box contains 1–9).
3. **Frontend grid input**: Type a puzzle cell-by-cell, click Solve, watch animation play. Verify highlighting moves across cells and red-flashes on backtrack.
4. **Paste input**: Paste an 81-char string, click "Load from text", verify grid populates correctly.
5. **Speed control**: Switch speed mid-animation; verify the next tick honors the new delay.
6. **No-visual mode**: Selecting "No visual" and solving paints the final board immediately.
7. **Validation**: Enter a board with duplicate 7s in one row, click Solve, verify error message appears and no animation runs.
8. **Unsolvable**: Enter a board that cannot be completed, verify the user sees "Unsolvable" after the search.
