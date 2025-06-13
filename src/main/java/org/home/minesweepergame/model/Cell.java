package org.home.minesweepergame.model;

public class Cell {
    public boolean isMine;
    public boolean isRevealed;
    public boolean isFlagged;
    public int adjacentMines;

    // Constructor
    public Cell(boolean isMine, boolean isRevealed, boolean isFlagged, int adjacentMines) {
        this.isMine = isMine;
        this.isRevealed = isRevealed;
        this.isFlagged = isFlagged;
        this.adjacentMines = adjacentMines;
    }

    // --- Getters ---
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

    // Optional: toString for debugging
    @Override
    public String toString() {
        return "Cell{" +
                "isMine=" + isMine +
                ", isRevealed=" + isRevealed +
                ", isFlagged=" + isFlagged +
                ", adjacentMines=" + adjacentMines +
                '}';
    }
}
