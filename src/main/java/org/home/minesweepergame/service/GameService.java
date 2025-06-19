package org.home.minesweepergame.service;

import org.home.minesweepergame.dtos.GameCreationRequest;
import org.home.minesweepergame.model.Cell;
import org.home.minesweepergame.model.Difficulty;
import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.model.GameStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, GameBoard> activeGames = new ConcurrentHashMap<>();

    // Define standard dimensions for difficulties here
    private static final Map<Difficulty, int[]> DIFFICULTY_SETTINGS = Map.of(
            Difficulty.EASY, new int[]{9, 9, 10},      // rows, cols, mines
            Difficulty.MEDIUM, new int[]{16, 16, 40},
            Difficulty.HARD, new int[]{16, 30, 99}
    );

    /**
     * Creates a new game board based on either a predefined difficulty or custom settings.
     * This replaces the old 'startGame' method.
     * @param request GameCreationRequest containing difficulty or custom dimensions.
     * @return The newly created GameBoard.
     * @throws IllegalArgumentException if invalid custom settings are provided.
     */
    public GameBoard createGame(GameCreationRequest request) {
        int rows, cols, mines;
        Difficulty difficultyType;

        if (request.getDifficulty() != null && request.getDifficulty() != Difficulty.CUSTOM) {
            difficultyType = request.getDifficulty();
            int[] settings = DIFFICULTY_SETTINGS.get(difficultyType);
            if (settings == null) {
                throw new IllegalArgumentException("Invalid predefined difficulty: " + difficultyType);
            }
            rows = settings[0];
            cols = settings[1];
            mines = settings[2];
        } else if (request.getRows() != null && request.getCols() != null && request.getMines() != null) {
            difficultyType = Difficulty.CUSTOM;
            rows = request.getRows();
            cols = request.getCols();
            mines = request.getMines();

            // Server-side validation for custom settings
            if (rows < 5 || cols < 5 || rows > 50 || cols > 50) {
                throw new IllegalArgumentException("Custom rows and columns must be between 5 and 50.");
            }
            if (mines < 1 || mines >= (rows * cols)) {
                throw new IllegalArgumentException("Number of mines must be at least 1 and less than total cells.");
            }
            if (mines > (rows * cols) / 2) {
                throw new IllegalArgumentException("Too many mines for the given board size (maximum 50% of cells).");
            }
        } else {
            throw new IllegalArgumentException("Invalid game creation request. Specify either a predefined difficulty or custom rows, columns, and mines.");
        }

        String gameId = UUID.randomUUID().toString();
        Cell[][] board = new Cell[rows][cols];
        initializeBoard(board, rows, cols); // Initialize cells before placing mines

        GameBoard newGame = new GameBoard(gameId, rows, cols, mines, board, GameStatus.NOT_STARTED, difficultyType);
        activeGames.put(gameId, newGame);

        System.out.println("New game created: " + newGame);
        return newGame;
    }

    // Existing methods (revealCell, flagCell, chordClick, getGame) will remain largely the same,
    // but ensure they use the GameBoard object retrieved from 'activeGames' map.

    public GameBoard getGame(String gameId) {
        return activeGames.get(gameId);
    }

    public GameBoard revealCell(String gameId, int row, int col) {
        GameBoard game = activeGames.get(gameId);
        if (game == null || game.getStatus() == GameStatus.WON || game.getStatus() == GameStatus.LOST) {
            return null; // Game not found or already ended
        }

        // Start the game and place mines on the first reveal if NOT_STARTED
        if (game.getStatus() == GameStatus.NOT_STARTED) {
            game.setStartTime(System.currentTimeMillis());
            placeMines(game, row, col); // Pass the clicked cell to avoid placing a mine there
            calculateAdjacentMines(game);
            game.setStatus(GameStatus.IN_PROGRESS);
        }

        Cell cell = game.getBoard()[row][col];

        if (cell.isRevealed() || cell.isFlagged()) {
            return game; // Do nothing if already revealed or flagged
        }

        cell.setRevealed(true);

        if (cell.isMine()) {
            game.setStatus(GameStatus.LOST);
            revealAllMines(game); // Show all mines on loss
            System.out.println("Game " + gameId + " LOST.");
        } else {
            if (cell.getAdjacentMines() == 0) {
                // If the revealed cell has 0 adjacent mines, auto-reveal its neighbors
                revealEmptyCells(game, row, col);
            }
            if (checkWinCondition(game)) {
                game.setStatus(GameStatus.WON);
                game.setTimeTaken(System.currentTimeMillis() - game.getStartTime());
                System.out.println("Game " + gameId + " WON. Time taken: " + game.getTimeTaken() + "ms.");
            }
        }
        return game;
    }

    public GameBoard flagCell(String gameId, int row, int col) {
        GameBoard game = activeGames.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return null;
        }

        Cell cell = game.getBoard()[row][col];
        if (!cell.isRevealed()) {
            cell.setFlagged(!cell.isFlagged()); // Toggle flag
        }
        return game;
    }

    public GameBoard chordClick(String gameId, int row, int col) {
        GameBoard game = activeGames.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return null;
        }

        Cell clickedCell = game.getBoard()[row][col];

        // Chord only works if the cell is already revealed and is not a mine
        if (!clickedCell.isRevealed() || clickedCell.isMine()) {
            return game;
        }

        int flaggedNeighbors = countFlaggedNeighbors(game, row, col);

        // If flagged neighbors match adjacent mines, reveal unflagged non-mine neighbors
        if (flaggedNeighbors == clickedCell.getAdjacentMines()) {
            for (int rOffset = -1; rOffset <= 1; rOffset++) {
                for (int cOffset = -1; cOffset <= 1; cOffset++) {
                    if (rOffset == 0 && cOffset == 0) continue;

                    int nRow = row + rOffset;
                    int nCol = col + cOffset;

                    if (isValidCoord(nRow, nCol, game.getRows(), game.getCols())) {
                        Cell neighborCell = game.getBoard()[nRow][nCol];
                        if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                            // Recursively call revealCell for non-flagged, unrevealed neighbors
                            // Note: This can potentially trigger a loss if a mine is revealed
                            revealCell(gameId, nRow, nCol); // Re-calling revealCell to ensure full game state update and win/loss checks
                        } else if (neighborCell.isFlagged() && !neighborCell.isMine()) {
                            // If a flagged neighbor is not a mine, it's an incorrect flag and chord click should fail
                            // For a true Minesweeper experience, incorrect flags on chord should result in loss
                            // We can uncomment the next lines to implement that strict rule
                            // game.setStatus(GameStatus.LOST);
                            // revealAllMines(game);
                            // return game;
                        }
                    }
                }
            }
        }
        return game;
    }

    // --- Helper Methods ---

    private void initializeBoard(Cell[][] board, int rows, int cols) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new Cell(r, c); // Ensure Cell constructor takes row and col
            }
        }
    }

    private void placeMines(GameBoard game, int initialRow, int initialCol) {
        Random random = new Random();
        int minesToPlace = game.getMines();
        int rows = game.getRows();
        int cols = game.getCols();

        while (minesToPlace > 0) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            // Ensure mine is not placed on the initial clicked cell or its immediate neighbors
            // to guarantee a safe first click.
            boolean isInitialClickArea = (Math.abs(r - initialRow) <= 1 && Math.abs(c - initialCol) <= 1);

            if (!game.getBoard()[r][c].isMine() && !isInitialClickArea) {
                game.getBoard()[r][c].setMine(true);
                minesToPlace--;
            }
        }
    }

    private void calculateAdjacentMines(GameBoard game) {
        int rows = game.getRows();
        int cols = game.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell currentCell = game.getBoard()[r][c];
                if (!currentCell.isMine()) {
                    int count = 0;
                    for (int rOffset = -1; rOffset <= 1; rOffset++) {
                        for (int cOffset = -1; cOffset <= 1; cOffset++) {
                            if (rOffset == 0 && cOffset == 0) continue; // Skip self

                            int nRow = r + rOffset;
                            int nCol = c + cOffset;

                            if (isValidCoord(nRow, nCol, rows, cols) && game.getBoard()[nRow][nCol].isMine()) {
                                count++;
                            }
                        }
                    }
                    currentCell.setAdjacentMines(count);
                }
            }
        }
    }

    private void revealEmptyCells(GameBoard game, int row, int col) {
        Queue<Cell> queue = new LinkedList<>();
        queue.add(game.getBoard()[row][col]);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            for (int rOffset = -1; rOffset <= 1; rOffset++) {
                for (int cOffset = -1; cOffset <= 1; cOffset++) {
                    if (rOffset == 0 && cOffset == 0) continue;

                    int nRow = current.getRow() + rOffset;
                    int nCol = current.getCol() + cOffset;

                    if (isValidCoord(nRow, nCol, game.getRows(), game.getCols())) {
                        Cell neighbor = game.getBoard()[nRow][nCol];
                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            neighbor.setRevealed(true);
                            if (neighbor.getAdjacentMines() == 0) {
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
    }

    private int countFlaggedNeighbors(GameBoard game, int row, int col) {
        int flagged = 0;
        for (int rOffset = -1; rOffset <= 1; rOffset++) {
            for (int cOffset = -1; cOffset <= 1; cOffset++) {
                if (rOffset == 0 && cOffset == 0) continue;

                int nRow = row + rOffset;
                int nCol = col + cOffset;

                if (isValidCoord(nRow, nCol, game.getRows(), game.getCols())) {
                    if (game.getBoard()[nRow][nCol].isFlagged()) {
                        flagged++;
                    }
                }
            }
        }
        return flagged;
    }

    private boolean checkWinCondition(GameBoard game) {
        int totalNonMines = (game.getRows() * game.getCols()) - game.getMines();
        int revealedNonMines = 0;
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                Cell currentCell = game.getBoard()[r][c];
                if (!currentCell.isMine() && currentCell.isRevealed()) {
                    revealedNonMines++;
                }
            }
        }
        return revealedNonMines == totalNonMines;
    }

    private void revealAllMines(GameBoard game) {
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                Cell currentCell = game.getBoard()[r][c];
                if (currentCell.isMine()) {
                    currentCell.setRevealed(true);
                }
            }
        }
    }

    private boolean isValidCoord(int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}