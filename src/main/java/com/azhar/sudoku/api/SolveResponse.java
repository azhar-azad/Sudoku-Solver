package com.azhar.sudoku.api;

import com.azhar.sudoku.solver.SolveResult;
import com.azhar.sudoku.solver.Step;

import java.util.List;

public class SolveResponse {

    private boolean solved;
    private int[][] finalBoard;
    private List<Step> steps;
    private int stepCount;
    private long elapsedMs;
    private String error;

    public static SolveResponse solved(SolveResult result, long elapsedMs) {
        SolveResponse response = new SolveResponse();
        response.solved = true;
        response.finalBoard = result.getFinalBoard();
        response.steps = result.getSteps();
        response.stepCount = result.getSteps().size();
        response.elapsedMs = elapsedMs;
        return response;
    }

    public static SolveResponse unsolvable(SolveResult result) {
        SolveResponse response = new SolveResponse();
        response.solved = false;
        response.steps = result.getSteps();
        response.stepCount = result.getSteps().size();
        response.error = "Sudoku has no solution";
        return response;
    }

    public static SolveResponse invalid(String error) {
        SolveResponse response = new SolveResponse();
        response.solved = false;
        response.error = error;
        return response;
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

    public int getStepCount() {
        return stepCount;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public String getError() {
        return error;
    }
}
