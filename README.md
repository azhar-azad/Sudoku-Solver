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
