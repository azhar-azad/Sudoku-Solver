package com.azhar.sudoku.solver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuValidatorTest {

    private SudokuValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SudokuValidator();
    }

    @Test
    void validBoardPassesValidation() {
        int[][] board = {
            {5,3,0, 0,7,0, 0,0,0},
            {6,0,0, 1,9,5, 0,0,0},
            {0,9,8, 0,0,0, 0,6,0},
            {8,0,0, 0,6,0, 0,0,3},
            {4,0,0, 8,0,3, 0,0,1},
            {7,0,0, 0,2,0, 0,0,6},
            {0,6,0, 0,0,0, 2,8,0},
            {0,0,0, 4,1,9, 0,0,5},
            {0,0,0, 0,8,0, 0,7,9}
        };

        ValidationResult result = validator.validate(board);
        assertTrue(result.isValid());
        assertNull(result.getError());
    }

    @Test
    void duplicateInRowFailsValidation() {
        int[][] board = new int[9][9];
        board[0][0] = 7;
        board[0][4] = 7;
        ValidationResult result = validator.validate(board);
        assertFalse(result.isValid());
        assertTrue(result.getError().contains("row"));
    }

    @Test
    void duplicateInColumnFailsValidation() {
        int[][] board = new int[9][9];
        board[0][0] = 5;
        board[4][0] = 5;
        ValidationResult result = validator.validate(board);
        assertFalse(result.isValid());
        assertTrue(result.getError().contains("column"));
    }

    @Test
    void duplicateInBoxFailsValidation() {
        int[][] board = new int[9][9];
        board[0][0] = 3;
        board[1][1] = 3;
        ValidationResult result = validator.validate(board);
        assertFalse(result.isValid());
        assertTrue(result.getError().contains("box"));
    }

    @Test
    void outOfRangeValueFailsValidation() {
        int[][] board = new int[9][9];
        board[2][2] = 10;
        ValidationResult result = validator.validate(board);
        assertFalse(result.isValid());

        assertTrue(result.getError().contains("Out-of-range"));
    }

}
