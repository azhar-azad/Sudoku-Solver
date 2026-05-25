import { renderGrid, readGrid, writeGrid, clearGrid, parsePaste } from './grid.js';
import { solve } from './api.js';
import { createAnimator } from './animator.js';

document.addEventListener('DOMContentLoaded', () => {
    const gridContainer = document.getElementById('grid');
    const pasteInput    = document.getElementById('paste-input');
    const loadBtn       = document.getElementById('load-btn');
    const pasteError    = document.getElementById('paste-error');
    const speedSelect   = document.getElementById('speed-select');
    const solveBtn      = document.getElementById('solve-btn');
    const pauseBtn      = document.getElementById('pause-btn');
    const resumeBtn     = document.getElementById('resume-btn');
    const resetBtn      = document.getElementById('reset-btn');
    const clearBtn      = document.getElementById('clear-btn');
    const status        = document.getElementById('status');

    const animator = createAnimator();
    let originalBoard  = null;
    let lastActiveCell = null;

    renderGrid(gridContainer);

    function getSpeedMs() {
        return parseInt(speedSelect.value);
    }

    function setControls({ solve, pause, resume, reset, clear }) {
        solveBtn.disabled  = !solve;
        loadBtn.disabled   = !solve;
        pauseBtn.disabled  = !pause;
        resumeBtn.disabled = !resume;
        resetBtn.disabled  = !reset;
        clearBtn.disabled  = !clear;
    }

    function clearActiveCell() {
        if (lastActiveCell) {
            lastActiveCell.classList.remove('cell-active', 'cell-backtrack');
            lastActiveCell = null;
        }
    }

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
        setControls({ solve: false, pause: false, resume: false, reset: false, clear: false });

        try {
            originalBoard = readGrid();
            const result  = await solve(originalBoard);

            // Validation failure — no animation
            if (!result.solved && result.error && !result.steps?.length) {
                status.textContent = 'Invalid puzzle: ' + result.error;
                setControls({ solve: true, pause: false, resume: false, reset: false, clear: true });
                return;
            }

            // No visual — paint result immediately
            if (getSpeedMs() === 0) {
                if (result.solved) {
                    writeGrid(result.finalBoard);
                    status.textContent = `Solved in ${result.stepCount} steps (${result.elapsedMs}ms)`;
                } else {
                    status.textContent = 'Unsolvable';
                }
                setControls({ solve: true, pause: false, resume: false, reset: true, clear: true });
                return;
            }

            // Animate
            const cells = document.querySelectorAll('.cell');
            setControls({ solve: false, pause: true, resume: false, reset: true, clear: false });

            animator.play(
                result.steps,
                getSpeedMs,
                (step) => {
                    clearActiveCell();
                    const cell = cells[step.row * 9 + step.col];
                    cell.value = step.value === 0 ? '' : step.value;
                    cell.classList.add(step.action === 'PLACE' ? 'cell-active' : 'cell-backtrack');
                    lastActiveCell = cell;
                },
                () => {
                    clearActiveCell();
                    status.textContent = result.solved
                        ? `Solved in ${result.stepCount} steps (${result.elapsedMs}ms)`
                        : 'Unsolvable';
                    setControls({ solve: true, pause: false, resume: false, reset: true, clear: true });
                }
            );
        } catch (err) {
            status.textContent = 'Error: ' + err.message;
            setControls({ solve: true, pause: false, resume: false, reset: false, clear: true });
        }
    });

    pauseBtn.addEventListener('click', () => {
        animator.pause();
        setControls({ solve: false, pause: false, resume: true, reset: true, clear: false });
    });

    resumeBtn.addEventListener('click', () => {
        animator.resume();
        setControls({ solve: false, pause: true, resume: false, reset: true, clear: false });
    });

    resetBtn.addEventListener('click', () => {
        animator.stop();
        clearActiveCell();
        if (originalBoard) writeGrid(originalBoard);
        status.textContent = '';
        setControls({ solve: true, pause: false, resume: false, reset: false, clear: true });
    });

    clearBtn.addEventListener('click', () => {
        animator.stop();
        clearActiveCell();
        clearGrid();
        originalBoard = null;
        status.textContent = '';
        setControls({ solve: true, pause: false, resume: false, reset: false, clear: true });
    });
});
