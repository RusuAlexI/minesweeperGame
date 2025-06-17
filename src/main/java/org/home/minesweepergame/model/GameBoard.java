package org.home.minesweepergame.model;

// Game.java
public class GameBoard {
    private String id;
    private int rows;
    private int cols;
    private int mines;
    private Cell[][] board;
    private GameStatus status; // This MUST be set correctly
    private long startTime; // Time in milliseconds when the game started
    private Long timeTaken; // Time in milliseconds when the game ended (null if not ended)
    private Difficulty difficulty; // New field

    // Constructor
    public GameBoard(String id, int rows, int cols, int mines, Cell[][] board,
                     GameStatus status, long startTime, Difficulty difficulty) { // Added Difficulty
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.board = board;
        this.status = status;
        this.startTime = startTime;
        this.difficulty = difficulty; // Set new field
        this.timeTaken = null; // Initialize as null, set on win/loss
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

    public Long getTimeTaken() { return timeTaken; }
    public void setTimeTaken(Long timeTaken) { this.timeTaken = timeTaken; }

    public Difficulty getDifficulty() { return difficulty; } // New getter
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; } // New setter

    // Optional: Helper to get elapsed time while game is in progress
    public long getElapsedTime() {
        if (status == GameStatus.IN_PROGRESS) {
            return System.currentTimeMillis() - startTime;
        }
        return (timeTaken != null) ? timeTaken : 0;
    }
}
