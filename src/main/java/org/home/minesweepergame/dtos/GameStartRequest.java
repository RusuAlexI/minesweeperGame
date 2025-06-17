package org.home.minesweepergame.dtos;
import org.home.minesweepergame.model.Difficulty; // Import the new Difficulty enum


public class GameStartRequest {
    private Difficulty difficulty; // Now directly use Difficulty enum
    private int rows;
    private int cols;
    private int mines;

    // Getter
    public Difficulty getDifficulty() {
        return difficulty;
    }

    // Setter
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    // Getters and Setters
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }
    public int getMines() { return mines; }
    public void setMines(int mines) { this.mines = mines; }
}