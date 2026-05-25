package com.azhar.sudoku.api;

import com.azhar.sudoku.solver.SolveResult;
import com.azhar.sudoku.solver.SudokuSolver;
import com.azhar.sudoku.solver.SudokuValidator;
import com.azhar.sudoku.solver.ValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SolveController {

    private final SudokuValidator validator = new SudokuValidator();
    private final SudokuSolver solver = new SudokuSolver();

    @PostMapping("/solve")
    public ResponseEntity<SolveResponse> solve(@RequestBody SolveRequest request) {
        if (request.getBoard() == null) {
            return ResponseEntity.badRequest().body(SolveResponse.invalid("Board is required"));
        }
        ValidationResult validation = validator.validate(request.getBoard());
        if (!validation.isValid()) {
            return ResponseEntity.badRequest().body(SolveResponse.invalid(validation.getError()));
        }

        long start = System.currentTimeMillis();
        SolveResult result = solver.solve(request.getBoard());
        long elapsed = System.currentTimeMillis() - start;

        if (result.isSolved()) {
            return ResponseEntity.ok(SolveResponse.solved(result, elapsed));
        } else {
            return ResponseEntity.ok(SolveResponse.unsolvable(result));
        }
    }
}
