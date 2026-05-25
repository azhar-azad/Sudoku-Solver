package com.azhar.sudoku.solver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuSolverTest {

    private SudokuSolver solver;

    @BeforeEach
    void setUp() {
        solver = new SudokuSolver();
    }

    @Test
    void solvesEasyPuzzle() {
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
        SolveResult result = solver.solve(board);
        assertTrue(result.isSolved());
        assertValidSolution(result.getFinalBoard());
        assertFalse(result.getSteps().isEmpty());
    }

    @Test
    void solvesHardPuzzle() {
        // Hard puzzle (28 clues) — requires meaningful backtracking but stays tractable
        // with a naive row-major solver (unlike true 17-clue minimum puzzles)
        int[][] board = {
                {0,0,0, 2,6,0, 7,0,1},
                {6,8,0, 0,7,0, 0,9,0},
                {1,9,0, 0,0,4, 5,0,0},
                {8,2,0, 1,0,0, 0,4,0},
                {0,0,4, 6,0,2, 9,0,0},
                {0,5,0, 0,0,3, 0,2,8},
                {0,0,9, 3,0,0, 0,7,4},
                {0,4,0, 0,5,0, 0,3,6},
                {7,0,3, 0,1,8, 0,0,0}
        };
        SolveResult result = solver.solve(board);
        assertTrue(result.isSolved());
        assertValidSolution(result.getFinalBoard());
    }

    @Test
    void detectsUnsolvablePuzzle() {
        // Two 5s in the same row — unsolvable
        int[][] board = new int[9][9];
        board[0][0] = 5;
        board[0][1] = 6;
        board[0][2] = 7;
        board[0][3] = 8;
        board[0][4] = 9;
        board[0][5] = 1;
        board[0][6] = 2;
        board[0][7] = 3;
        // row 0 col 8 is missing — but the remaining 8 rows leave no valid digit for it
        // Force unsolvable: fill in contradictions across rows
        board[1][8] = 4;
        board[2][8] = 5;
        board[3][8] = 6;
        board[4][8] = 7;
        board[5][8] = 8;
        board[6][8] = 9;
        board[7][8] = 1;
        board[8][8] = 2;
        SolveResult result = solver.solve(board);
        assertFalse(result.isSolved());
        assertNotNull(result.getSteps()); // steps still populated from the failed search
    }


    private void assertValidSolution(int[][] board) {
        // Every row, column, and 3x3 box must contain digits 1–9 exactly once
        for (int i = 0; i < 9; i++) {
            boolean[] rowSeen = new boolean[10];
            boolean[] colSeen = new boolean[10];
            for (int j = 0; j < 9; j++) {
                int rv = board[i][j];
                int cv = board[j][i];
                assertTrue(rv >= 1 && rv <= 9, "Row " + i +
                    " has invalid value " + rv);
                assertTrue(cv >= 1 && cv <= 9, "Col " + i +
                    " has invalid value " + cv);
                assertFalse(rowSeen[rv], "Duplicate " + rv + " in row " + i);
                assertFalse(colSeen[cv], "Duplicate " + cv + " in col " + i);
                rowSeen[rv] = true;
                colSeen[cv] = true;
            }
        }
        for (int boxRow = 0; boxRow < 9; boxRow += 3) {
            for (int boxCol = 0; boxCol < 9; boxCol += 3) {
                boolean[] seen = new boolean[10];
                for (int i = boxRow; i < boxRow + 3; i++) {
                    for (int j = boxCol; j < boxCol + 3; j++)
                    {
                        int v = board[i][j];
                        assertFalse(seen[v], "Duplicate " + v
                                + " in box at " + boxRow + "," + boxCol);
                        seen[v] = true;
                    }
                }
            }
        }
    }
}
