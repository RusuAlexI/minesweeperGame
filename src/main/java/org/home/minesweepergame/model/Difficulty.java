package org.home.minesweepergame.model;

public enum Difficulty {
        // Standard Minesweeper sizes:
        // EASY:    8x8 board, 10 mines
        // MEDIUM: 16x16 board, 40 mines
        // HARD:   30x16 board, 99 mines (width x height)
        // Note: The original game uses width then height, so (cols, rows) for board dimension.
        // My previous code used (rows, cols), so I'll stick to that for consistency with current code.

        EASY(8, 8, 10),
        MEDIUM(16, 16, 40),
        HARD(16, 30, 99); // Rows, Cols, Mines - Adjusted HARD to be 16 rows, 30 cols

        private final int rows;
        private final int cols;
        private final int mines;

        Difficulty(int rows, int cols, int mines) {
            this.rows = rows;
            this.cols = cols;
            this.mines = mines;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public int getMines() {
            return mines;
        }
}
