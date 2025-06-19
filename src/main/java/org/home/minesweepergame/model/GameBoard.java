package org.home.minesweepergame.model;

import java.util.UUID; // Ensure UUID is imported

// Represents the entire Minesweeper game board and its state.
public class GameBoard {
    private String gameId;
    private int rows;
    private int cols;
    private int mines;
    private Cell[][] board;
    private GameStatus status;
    private long startTime;    // Timestamp when the game started (first reveal)
    private long timeTaken;    // Time taken to win the game (in milliseconds)
    private Difficulty difficulty; // The difficulty level for this game (EASY, MEDIUM, HARD, CUSTOM)

    // No-argument constructor for serialization/deserialization (e.g., by Spring's JSON converter)
    public GameBoard() {
        this.gameId = UUID.randomUUID().toString(); // Generate ID by default
        this.status = GameStatus.NOT_STARTED; // Default status
    }

    // Constructor used by GameService to initialize a new game board.
    // It does NOT place mines or calculate adjacent mines; that's handled by GameService.
    public GameBoard(String gameId, int rows, int cols, int mines, Cell[][] board, GameStatus status, Difficulty difficulty) {
        this.gameId = gameId;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.board = board; // The board is initialized with empty cells here, mines placed later
        this.status = status;
        this.difficulty = difficulty;
        this.startTime = 0; // Will be set on first reveal
        this.timeTaken = 0; // Will be set on win
    }

    // --- Getters ---
    public String getGameId() {
        return gameId;
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

    public Cell[][] getBoard() {
        return board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    // --- Setters ---
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game ID: ").append(gameId).append("\n");
        sb.append("Difficulty: ").append(difficulty).append("\n");
        sb.append("Dimensions: ").append(rows).append("x").append(cols).append("\n");
        sb.append("Mines: ").append(mines).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Time Taken: ").append(timeTaken > 0 ? (timeTaken / 1000.0) + "s" : "N/A").append("\n");
        sb.append("Board:\n");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sb.append(board[r][c].toString()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}