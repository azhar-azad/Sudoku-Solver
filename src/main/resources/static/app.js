import { renderGrid, readGrid, writeGrid, parsePaste } from './grid.js';
import { solve } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
    const gridContainer = document.getElementById('grid');
    const pasteInput    = document.getElementById('paste-input');
    const loadBtn       = document.getElementById('load-btn');
    const pasteError    = document.getElementById('paste-error');
    const solveBtn      = document.getElementById('solve-btn');
    const status        = document.getElementById('status');

    renderGrid(gridContainer);

    loadBtn.addEventListener('click', () => {
        pasteError.textContent = '';
        const board = parsePaste(pasteInput.value);
        if (board === null) {
            pasteError.textContent = 'Invalid input — enter exactly 81 digits (use 0 or . for blanks)';
            return;
        }
        writeGrid(board);
        pasteInput.value = '';
    });

    solveBtn.addEventListener('click', async () => {
        status.textContent = 'Solving…';
        solveBtn.disabled = true;

        try {
            const board = readGrid();
            const result = await solve(board);

            if (!result.solved && result.error && !result.steps?.length) {
                status.textContent = 'Invalid puzzle: ' + result.error;
            } else if (!result.solved) {
                status.textContent = 'Unsolvable';
            } else {
                writeGrid(result.finalBoard);
                status.textContent = `Solved in ${result.stepCount} steps (${result.elapsedMs}ms)`;
            }
        } catch (err) {
            status.textContent = 'Error: ' + err.message;
        } finally {
            solveBtn.disabled = false;
        }
    });
});
