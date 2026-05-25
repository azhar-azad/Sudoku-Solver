package com.azhar.sudoku.solver;

import java.util.ArrayList;
import java.util.List;

public class SudokuSolver {

    public SolveResult solve(int[][] inputBoard) {
        int[][] board = copyBoard(inputBoard);
        List<Step> steps = new ArrayList<>();
        boolean solved = backtrack(board, steps);
        return new SolveResult(solved, board, steps);
    }

    private boolean backtrack(int[][] board, List<Step> steps) {
        int[] cell = findNextEmpty(board);
        if (cell == null) return true;

        int row = cell[0];
        int col = cell[1];

        for (int digit = 1; digit <= 9; digit++) {
            if (isValid(board, row, col, digit)) {
                board[row][col] = digit;
                steps.add(new Step(row, col, digit, Action.PLACE));

                if (backtrack(board, steps)) return true;

                board[row][col] = 0;
                steps.add(new Step(row, col, 0, Action.BACKTRACK));
            }
        }
        return false;
    }

    private int[] findNextEmpty(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) return new int[]{i, j};
            }
        }
        return null;
    }

    private boolean isValid(int[][] board, int row, int col, int digit) {
        for (int j = 0; j < 9; j++) {
            if (board[row][j] == digit) return false;
        }
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == digit) return false;
        }
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == digit) return false;
            }
        }
        return true;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            // Java arrays are reference types - we don't want to touch the original input
            System.arraycopy(board[i], 0, copy[i], 0, 9);
        }
        return copy;
    }
}
