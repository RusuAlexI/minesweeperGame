package org.home.minesweepergame.model;

// Game.java
public class GameBoard {
    private String id;
    private int rows;
    private int cols;
    private int mines;
    private Cell[][] board;
    private GameStatus status; // This MUST be set correctly
    private long startTime;    // This MUST be set correctly

    // Constructor used by GameService.startGame
    public GameBoard(String id, int rows, int cols, int mines, Cell[][] board, GameStatus status, long startTime) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.board = board;
        this.status = status; // Ensure this is assigning GameStatus.IN_PROGRESS
        this.startTime = startTime; // Ensure this is assigning System.currentTimeMillis()
    }

    // Default constructor (required by Jackson for deserialization)
    public GameBoard() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
