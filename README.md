# Sudoku Solver

A web application that lets you input an incomplete Sudoku puzzle and watch a backtracking algorithm solve it step by step, with adjustable animation speed.

## Prerequisites

- Java 21+
- No Maven installation needed — the project uses the Maven Wrapper (`mvnw`)

## Run

```bash
./mvnw spring-boot:run
```

Then open [http://localhost:8080](http://localhost:8080).

## Features

- **9×9 input grid** — type digits directly into cells (1–9 only)
- **Paste input** — paste an 81-character string (`0` or `.` for blanks) and click Load
- **Backtracking solver** — pure Java recursive solver records every placement and backtrack
- **Step-by-step animation** — watch the algorithm work in real time with cell highlighting:
  - Green flash for each digit placed
  - Red flash for each backtrack
- **Adjustable speed** — Slow (300ms) / Regular (100ms) / Fast (30ms) / Very Fast (5ms) / No visual
- **Pause / Resume / Reset** — full animation control; Reset restores your original input
- **Clear board** — wipe the grid to start fresh
- **Pre-solve validation** — duplicate detection before solving; clear error messages shown
- **Unsolvable detection** — exhaustive search with step replay, then "Unsolvable" status

## Test

```bash
# All tests
./mvnw test

# Single test class
./mvnw test -Dtest=SudokuSolverTest

# Single test method
./mvnw test -Dtest=SudokuSolverTest#solvesHardPuzzle
```

## Build

```bash
./mvnw clean package -DskipTests
```

## API

```
POST /api/solve
Content-Type: application/json

{ "board": [[5,3,0,...], ...] }   // 9×9, 0 = blank
```

Success response:
```json
{ "solved": true, "finalBoard": [[...]], "steps": [...], "stepCount": 1247, "elapsedMs": 38 }
```

---

## Project Bootstrap

The Spring Boot skeleton was generated using [Spring Initializr](https://start.spring.io) via curl:

```bash
curl "https://start.spring.io/starter.zip" \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=4.0.6 \
  -d groupId=com.azhar \
  -d artifactId=sudoku-solver \
  -d name=sudoku-solver \
  -d packageName=com.azhar.sudoku \
  -d javaVersion=21 \
  -d dependencies=web \
  -o _initializr.zip
```
