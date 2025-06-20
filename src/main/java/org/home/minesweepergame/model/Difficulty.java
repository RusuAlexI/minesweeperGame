package org.home.minesweepergame.model;

public enum Difficulty {
    EASY(9, 9, 10),      // rows, cols, mines
    MEDIUM(16, 16, 40),
    HARD(16, 30, 99),
    CUSTOM(0, 0, 0); // <--- ADD THIS LINE: Add CUSTOM with placeholder values

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