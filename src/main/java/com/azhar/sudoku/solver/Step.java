package com.azhar.sudoku.solver;

public record Step(
        int row,
        int col,
        int value,
        Action action
) {
}
