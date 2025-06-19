package org.home.minesweepergame.dtos;

import org.home.minesweepergame.model.Difficulty;

// This DTO allows clients to request either a predefined difficulty
// OR custom dimensions for rows, columns, and mines.
public class GameCreationRequest {
    private Difficulty difficulty; // Optional: for predefined difficulties (e.g., EASY, MEDIUM, HARD)
    private Integer rows;        // Optional: for custom games (number of rows)
    private Integer cols;        // Optional: for custom games (number of columns)
    private Integer mines;       // Optional: for custom games (number of mines)

    // Getters
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Integer getRows() {
        return rows;
    }

    public Integer getCols() {
        return cols;
    }

    public Integer getMines() {
        return mines;
    }

    // Setters
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setCols(Integer cols) {
        this.cols = cols;
    }

    public void setMines(Integer mines) {
        this.mines = mines;
    }

    @Override
    public String toString() {
        return "GameCreationRequest{" +
                "difficulty=" + difficulty +
                ", rows=" + rows +
                ", cols=" + cols +
                ", mines=" + mines +
                '}';
    }
}