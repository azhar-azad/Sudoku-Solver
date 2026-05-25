# Progress Tracker — Sudoku Solver Webapp

This is the granular execution checklist for the project described in [PLAN.md](PLAN.md). Each unchecked item below corresponds to **one git commit**. Commit messages should mirror the checklist line (without the leading `[ ]`).

Tick `[x]` when the commit is made. Work top-to-bottom — items within a phase often depend on earlier ones.

---

## Phase 0 — Repository bootstrap

- [ ] Initialize git repository in project root
- [ ] Add `.gitignore` for Maven (`target/`), IDE files (`.idea/`, `.vscode/`, `*.iml`), and OS files (`Thumbs.db`, `.DS_Store`)
- [ ] Commit `PLAN.md` and `PROGRESS_TRACKER.md` as the initial documentation commit

## Phase 1 — Spring Boot skeleton

- [ ] Add `pom.xml` with Spring Boot 3.x parent, Java 21, and `spring-boot-starter-web` dependency
- [ ] Add `src/main/resources/application.properties` with `server.port=8080`
- [ ] Create `SudokuApplication.java` with `@SpringBootApplication` and `main` method
- [ ] Verify the app boots: `mvn spring-boot:run` starts on port 8080 (manual check, no commit needed)

## Phase 2 — Solver core (pure Java, no Spring)

- [x] Add `Action` enum (`PLACE`, `BACKTRACK`) in `com.azhar.sudoku.solver`
- [x] Add `Step` record (`int row, int col, int value, Action action`) in `com.azhar.sudoku.solver`
- [x] Implement `SudokuValidator` with `validate(int[][] board)` returning a result object containing `valid` flag and optional error message
- [x] Add `SudokuValidatorTest` covering: valid board, duplicate in row, duplicate in column, duplicate in 3x3 box, out-of-range value
- [x] Implement `SudokuSolver` with backtracking that records every `PLACE` and `BACKTRACK` step
- [x] Add `SudokuSolverTest` for a known easy puzzle (verify final board correctness)
- [x] Add `SudokuSolverTest` case for a hard puzzle (28 clues — 17-clue minimum puzzles exceed memory for naive row-major backtracking)
- [x] Add `SudokuSolverTest` case for an unsolvable puzzle (verify returns no-solution result with steps populated)
- [x] Run `./mvnw test` — all 9 tests pass

## Phase 3 — REST API

- [x] Add `SolveRequest` DTO (`int[][] board`)
- [x] Add `SolveResponse` DTO (`boolean solved, int[][] finalBoard, List<Step> steps, int stepCount, long elapsedMs, String error`)
- [x] Add `SolveController` with `POST /api/solve`: runs validator, then solver, returns response
- [x] Add `GlobalExceptionHandler` (`@ControllerAdvice`) to convert validation and unexpected errors into clean JSON
- [x] Add `SolveControllerTest` covering valid, invalid, and unsolvable payloads via MockMvc — all 12 tests pass

## Phase 4 — Frontend grid and paste input

- [x] Add `src/main/resources/static/index.html` with page skeleton (title, container divs for grid/controls/status)
- [x] Add `styles.css` with CSS-grid 9x9 layout and thick 3x3 box borders
- [x] Add `grid.js` exporting `renderGrid(container)`, `readGrid()`, `writeGrid(board)`
- [x] Wire grid render on page load via `app.js`
- [x] Add paste textarea + "Load from text" button to `index.html`
- [x] Implement paste-string parser (81 chars, accepts `0` or `.` for blanks) in `grid.js` and wire button in `app.js`
- [x] Style invalid paste input with a small inline error message

## Phase 5 — Frontend ↔ backend wiring (no animation yet)

- [ ] Add `api.js` exporting `solve(board)` that POSTs to `/api/solve` and returns parsed JSON
- [ ] Add "Solve" button in `index.html` and handler in `app.js`: read grid → call api → write `finalBoard`
- [ ] Display status messages (`Solving…`, `Solved`, `Invalid puzzle: …`, `Unsolvable`) in the status area
- [ ] Manually verify full round-trip in browser: type puzzle, click Solve, see solution appear instantly

## Phase 6 — Animator with speed control

- [ ] Add speed selector to `index.html` (Slow / Regular / Fast / Very Fast / No visual)
- [ ] Add `animator.js` with `play(steps, speedMs, onStep, onDone)` using `setTimeout` recursion
- [ ] Wire animator into `app.js`: instead of writing `finalBoard` directly, replay `steps`
- [ ] Add `.cell-active` CSS class and apply it to the cell currently being changed
- [ ] Add `.cell-backtrack` CSS class (red flash) for `BACKTRACK` action steps
- [ ] Read current speed at each tick (so changing speed mid-animation takes effect on the next step)
- [ ] Add Pause and Resume buttons to `index.html` and wire them to the animator
- [ ] Add Reset button: stops animation, clears computed cells back to original input

## Phase 7 — No-visual mode and polish

- [ ] When "No visual" is selected, skip animation and write `finalBoard` immediately
- [ ] Display step count and elapsed solve time from the response in the status area
- [ ] Disable Solve button while solving/animating; re-enable on done or reset
- [ ] Add a "Clear board" button that resets the input grid entirely
- [ ] Tidy CSS: readable fonts, sensible cell size, visible focus state for keyboard input

## Phase 8 — README and final verification

- [ ] Add `README.md` with: prerequisites (Java 21, Maven), `mvn spring-boot:run`, open `http://localhost:8080`, brief feature list
- [ ] Run full verification plan from `PLAN.md` end-to-end (all 8 checks) and fix anything broken
- [ ] Final commit tagging the project as v1.0 (optional)

---

## Commit convention

Each commit message should be the checklist line text, e.g.:

```
Add Step record and Action enum
Implement SudokuSolver with backtracking and step recording
Wire animator into app.js for step replay
```

Keep commits small and focused — one logical change per commit. If a checklist item proves too large mid-implementation, split it into sub-items here first, then commit each sub-item separately.
