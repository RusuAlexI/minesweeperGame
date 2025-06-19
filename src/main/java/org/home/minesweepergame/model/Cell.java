package org.home.minesweepergame.model;

// Represents a single cell on the Minesweeper game board.
public class Cell {
    private int row; // Row coordinate of the cell
    private int col; // Column coordinate of the cell
    private boolean isMine;       // True if this cell contains a mine
    private boolean isRevealed;   // True if this cell has been revealed by the player
    private boolean isFlagged;    // True if the player has marked this cell with a flag
    private int adjacentMines;    // Number of adjacent cells containing mines

    // No-argument constructor for serialization (e.g., by Spring's JSON converter)
    public Cell() {
        // Default values for an uninitialized cell
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.adjacentMines = 0;
    }

    // Constructor to initialize a cell with its coordinates and default state
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.adjacentMines = 0;
    }

    // --- Getters ---
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    // --- Setters ---
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    @Override
    public String toString() {
        // This toString provides a helpful representation for debugging
        if (isRevealed) {
            if (isMine) {
                return "[M]"; // Mine revealed
            } else {
                return "[" + adjacentMines + "]"; // Revealed safe cell with mine count
            }
        } else if (isFlagged) {
            return "[F]"; // Flagged cell
        } else {
            return "[_]"; // Unrevealed, unflagged cell
        }
    }
}