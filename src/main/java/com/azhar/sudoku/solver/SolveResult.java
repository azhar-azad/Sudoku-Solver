package com.azhar.sudoku.solver;

import java.util.List;

public class SolveResult {

    private final boolean solved;
    private final int[][] finalBoard;
    private final List<Step> steps;

    public SolveResult(boolean solved, int[][] finalBoard, List<Step> steps) {
        this.solved = solved;
        this.finalBoard = finalBoard;
        this.steps = steps;
    }

    public boolean isSolved() {
        return solved;
    }

    public int[][] getFinalBoard() {
        return finalBoard;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
