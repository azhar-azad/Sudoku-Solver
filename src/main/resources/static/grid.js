const SIZE = 9;

export function renderGrid(container) {
    container.innerHTML = '';
    for (let i = 0; i < SIZE * SIZE; i++) {
        const input = document.createElement('input');
        input.type = 'number';
        input.min = '1';
        input.max = '9';
        input.className = 'cell';
        input.addEventListener('input', () => {
            const val = input.value.replace(/[^1-9]/g, '');
            input.value = val.length > 0 ? val[val.length - 1] : '';
        });
        container.appendChild(input);
    }
}

export function readGrid() {
    const cells = document.querySelectorAll('.cell');
    const board = Array.from({ length: SIZE }, () => new Array(SIZE).fill(0));
    cells.forEach((cell, i) => {
        const val = parseInt(cell.value);
        if (!isNaN(val) && val >= 1 && val <= 9) {
            board[Math.floor(i / SIZE)][i % SIZE] = val;
        }
    });
    return board;
}

export function writeGrid(board) {
    const cells = document.querySelectorAll('.cell');
    cells.forEach((cell, i) => {
        const val = board[Math.floor(i / SIZE)][i % SIZE];
        cell.value = val === 0 ? '' : val;
        cell.classList.remove('cell-given', 'cell-active', 'cell-backtrack');
        if (val !== 0) cell.classList.add('cell-given');
    });
}

export function parsePaste(str) {
    const cleaned = str.trim().replace(/\./g, '0');
    if (cleaned.length !== 81 || !/^[0-9]+$/.test(cleaned)) return null;
    const board = Array.from({ length: SIZE }, () => new Array(SIZE).fill(0));
    for (let i = 0; i < 81; i++) {
        board[Math.floor(i / SIZE)][i % SIZE] = parseInt(cleaned[i]);
    }
    return board;
}
