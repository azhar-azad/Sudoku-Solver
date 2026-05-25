import { renderGrid, writeGrid, parsePaste } from './grid.js';

document.addEventListener('DOMContentLoaded', () => {
    const gridContainer = document.getElementById('grid');
    const pasteInput    = document.getElementById('paste-input');
    const loadBtn       = document.getElementById('load-btn');
    const pasteError    = document.getElementById('paste-error');

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
});
