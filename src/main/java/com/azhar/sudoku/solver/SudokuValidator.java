package com.azhar.sudoku.solver;

public class SudokuValidator {

    public ValidationResult validate(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int val = board[i][j];
                if (val == 0) continue;
                if (val < 1 || val > 9) {
                    return ValidationResult.fail("Out-of-range value " + val + " at row "
                    + (i + 1) + ", col " + (j + 1));
                }
                if (hasDuplicateInRow(board, i, j, val)) {
                    return ValidationResult.fail("Duplicate " + val + " in row " + (i + 1));
                }
                if (hasDuplicateInCol(board, i, j, val)) {
                    return ValidationResult.fail("Duplicate " + val + " in column " + (j + 1));
                }
                if (hasDuplicateInBox(board, i, j, val)) {
                    return ValidationResult.fail("Duplicate " + val + " in 3x3 box");
                }
            }
        }
        return ValidationResult.ok();
    }

    private boolean hasDuplicateInRow(int[][] board, int row, int col, int val) {
        for (int j = 0; j < 9; j++) {
            if (j != col && board[row][j] == val) return true;
        }
        return false;
    }

    private boolean hasDuplicateInCol(int[][] board, int row, int col, int val) {
        for (int i = 0; i < 9; i++) {
            if (i != row && board[i][col] == val) return true;
        }
        return false;
    }

    private boolean hasDuplicateInBox(int[][] board, int row, int col, int val) {
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (!(i == row && j == col) && board[i][j] == val) return true;
            }
        }
        return false;
    }
}
