# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A Sudoku solver webapp: the user inputs a puzzle (grid or paste), clicks Solve, and watches a backtracking algorithm fill in the board visually with controllable speed. The frontend is served as static files by the Spring Boot backend, so there is no separate frontend server or CORS config.

See [PLAN.md](PLAN.md) for the full architecture decision record and [PROGRESS_TRACKER.md](PROGRESS_TRACKER.md) for the granular commit-by-commit checklist.

## Common commands

```bash
# Build (skip tests)
mvn clean package -DskipTests

# Run the app — serves frontend at http://localhost:8080
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=SudokuSolverTest

# Run a single test method
mvn test -Dtest=SudokuSolverTest#solvesHardPuzzle
```

## Architecture

```
Browser (vanilla HTML/CSS/JS — no framework, no build step)
  └── POST /api/solve  →  Spring Boot (Java 21, single module)
                               ├── SudokuValidator   (pre-flight rule check)
                               └── SudokuSolver      (backtracking, records every step)
                         Response: { solved, finalBoard, steps[], stepCount, elapsedMs }
  └── animator.js replays steps[] at user-chosen speed client-side
```

## Package layout

Root package: `com.azhar.sudoku`

| Package | Responsibility |
|---|---|
| `api` | `SolveController` (POST /api/solve), `SolveRequest`, `SolveResponse` |
| `solver` | `SudokuSolver`, `SudokuValidator`, `Step` record, `Action` enum |
| `exception` | `GlobalExceptionHandler` (`@ControllerAdvice` for clean error JSON) |

Frontend lives in `src/main/resources/static/` — Spring Boot serves it automatically.

| File | Responsibility |
|---|---|
| `index.html` | Page shell, 9×9 grid inputs, paste textarea, speed selector, buttons |
| `styles.css` | CSS-grid layout, 3×3 box borders, `.cell-active` / `.cell-backtrack` classes |
| `grid.js` | `renderGrid()`, `readGrid()`, `writeGrid(board)`, paste-string parser |
| `api.js` | `solve(board)` — POST wrapper, returns parsed JSON |
| `animator.js` | `play(steps, speedMs, onStep, onDone)` — `setTimeout`-based replay with pause support |
| `app.js` | Entry point — wires DOM events, coordinates grid/api/animator modules |

## Key design decisions

**Step delivery**: The solver records every `PLACE` and `BACKTRACK` move while solving and returns the full list in a single JSON response. The frontend then replays the list at the chosen speed (Slow 300ms / Regular 100ms / Fast 30ms / Very Fast 5ms). This keeps speed control purely client-side and allows pause/resume/speed-change without re-requesting.

**Step format**: `{ "row": 0, "col": 2, "value": 4, "action": "PLACE" }`. Action `BACKTRACK` always has `value: 0` (cell erased).

**Unsolvable puzzles**: The solver still returns the accumulated steps so the user can watch the exhaustive failed search. Response has `solved: false` and HTTP 200.

**Validation failures**: HTTP 400 with `{ "solved": false, "error": "Duplicate 7 in row 3" }`. No animation runs.

**Input format**: `int[9][9]` where `0` = blank. The paste textarea accepts an 81-character string (`0` or `.` for blanks).

## IDE

The project uses IntelliJ IDEA. JDK 21 should be configured as the project SDK (aligned with `pom.xml`). The `.idea/` directory is committed with the exception of files listed in `.idea/.gitignore`.
