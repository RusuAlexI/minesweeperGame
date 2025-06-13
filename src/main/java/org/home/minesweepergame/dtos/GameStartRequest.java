package org.home.minesweepergame.dtos;

public class GameStartRequest {
    private int rows;
    private int cols;
    private int mines;

    // Getters and Setters
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }
    public int getMines() { return mines; }
    public void setMines(int mines) { this.mines = mines; }
}