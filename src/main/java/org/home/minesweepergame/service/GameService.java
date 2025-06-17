package org.home.minesweepergame.service;

import org.home.minesweepergame.model.Cell;
import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.model.GameStatus;
import org.springframework.stereotype.Service;
import org.home.minesweepergame.model.Difficulty; // Import new Difficulty enum


import java.util.*;

@Service
public class GameService {
    private final Map<String, GameBoard> games = new HashMap<>();

    public GameBoard startGame(Difficulty difficulty) { // Modified signature
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null.");
        }

        int rows = difficulty.getRows();
        int cols = difficulty.getCols();
        int mines = difficulty.getMines();

        // Basic parameter validation (already handled by Difficulty enum, but good to keep general check)
        if (rows <= 0 || cols <= 0 || mines < 0 || mines >= rows * cols) {
            throw new IllegalArgumentException("Invalid game parameters for difficulty: " + difficulty.name());
        }

        String gameId = UUID.randomUUID().toString();
        Cell[][] board = initializeBoard(rows, cols);
        placeMines(board, mines);
        calculateAdjacentMines(board);

        GameBoard game = new GameBoard(
                gameId,
                rows,
                cols,
                mines,
                board,
                GameStatus.IN_PROGRESS,
                System.currentTimeMillis(), // Set startTime
                difficulty // Pass difficulty
        );
        games.put(gameId, game);
        return game;
    }

    // Renamed for clarity: initialize cells on the board
    private Cell[][] initializeBoard(int rows, int cols) {
        Cell[][] board = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell(false, false, false, 0); // isMine, isRevealed, isFlagged, adjacentMines
            }
        }
        return board;
    }

    // Method to place mines on a given board
    private void placeMines(Cell[][] board, int mines) {
        int rows = board.length;
        int cols = board[0].length;
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < mines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (!board[r][c].isMine()) {
                board[r][c].setMine(true);
                minesPlaced++;
            }
        }
    }

    // Method to calculate adjacent mines for all cells on the board
    private void calculateAdjacentMines(Cell[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!board[r][c].isMine()) { // Only calculate for non-mine cells
                    int count = 0;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0) continue; // Skip current cell

                            int nr = r + dr;
                            int nc = c + dc;

                            if (isValidCoord(nr, nc, rows, cols) && board[nr][nc].isMine()) {
                                count++;
                            }
                        }
                    }
                    board[r][c].setAdjacentMines(count);
                }
            }
        }
    }

    public GameBoard getGame(String id) {
        return games.get(id);
    }

    // Consolidated and corrected reveal logic (was 'reveal' and 'revealCell')
    public GameBoard revealCell(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return game;
        }

        if (!isValidCoord(row, col, game.getRows(), game.getCols())) {
            throw new IllegalArgumentException("Invalid cell coordinates.");
        }

        Cell cell = game.getBoard()[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return game;
        }

        cell.setRevealed(true);

        if (cell.isMine()) {
            game.setStatus(GameStatus.LOST);
            revealAllMines(game);
            game.setTimeTaken(System.currentTimeMillis() - game.getStartTime()); // Set time on loss
        } else if (cell.getAdjacentMines() == 0) {
            floodReveal(game, row, col);
        }

        if (game.getStatus() == GameStatus.IN_PROGRESS && checkWinCondition(game)) {
            game.setStatus(GameStatus.WON);
            game.setTimeTaken(System.currentTimeMillis() - game.getStartTime()); // Set time on win
        }

        games.put(gameId, game);
        return game;
    }

    // Consolidated and corrected flood fill logic (was 'floodReveal' and 'revealAdjacentEmptyCells')
    private void floodReveal(GameBoard game, int startRow, int startCol) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol});
        Cell[][] board = game.getBoard();
        int rows = game.getRows();
        int cols = game.getCols();

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;

                    int nr = r + dr;
                    int nc = c + dc;

                    if (isValidCoord(nr, nc, rows, cols)) {
                        Cell neighbor = board[nr][nc];
                        if (!neighbor.isRevealed() && !neighbor.isMine() && !neighbor.isFlagged()) {
                            neighbor.setRevealed(true);
                            if (neighbor.getAdjacentMines() == 0) {
                                queue.offer(new int[]{nr, nc}); // Continue flood fill
                            }
                        }
                    }
                }
            }
        }
    }

    // Consolidated and corrected flag toggle logic (was 'toggleFlag' and 'flagCell')
    public GameBoard flagCell(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return game;
        }

        if (!isValidCoord(row, col, game.getRows(), game.getCols())) {
            throw new IllegalArgumentException("Invalid cell coordinates.");
        }

        Cell cell = game.getBoard()[row][col];
        if (!cell.isRevealed()) { // Only allow flagging unrevealed cells
            cell.setFlagged(!cell.isFlagged()); // Toggle flag
        }
        games.put(gameId, game); // Update the game in the map
        return game;
    }


    // This method handles the 'chord click' logic
    public GameBoard chordClick(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Game not found or not in progress.");
        }

        Cell clickedCell = game.getBoard()[row][col];
        if (!clickedCell.isRevealed() || clickedCell.isMine()) {
            return game;
        }

        int flaggedNeighbors = countFlaggedNeighbors(game, row, col);

        if (flaggedNeighbors == clickedCell.getAdjacentMines()) {
            boolean mineHitDuringChord = false;
            for (int rOffset = -1; rOffset <= 1; rOffset++) {
                for (int cOffset = -1; cOffset <= 1; cOffset++) {
                    if (rOffset == 0 && cOffset == 0) continue;

                    int nRow = row + rOffset;
                    int nCol = col + cOffset;

                    if (isValidCoord(nRow, nCol, game.getRows(), game.getCols())) {
                        Cell neighborCell = game.getBoard()[nRow][nCol];

                        if (!neighborCell.isRevealed() && !neighborCell.isFlagged()) {
                            if (neighborCell.isMine()) {
                                neighborCell.setRevealed(true);
                                game.setStatus(GameStatus.LOST);
                                revealAllMines(game);
                                game.setTimeTaken(System.currentTimeMillis() - game.getStartTime()); // Set time on loss
                                mineHitDuringChord = true;
                                break;
                            } else {
                                if (neighborCell.getAdjacentMines() == 0) {
                                    floodReveal(game, nRow, nCol);
                                } else {
                                    neighborCell.setRevealed(true);
                                }
                            }
                        }
                    }
                }
                if (mineHitDuringChord) break;
            }
        }

        if (game.getStatus() == GameStatus.IN_PROGRESS && checkWinCondition(game)) {
            game.setStatus(GameStatus.WON);
            game.setTimeTaken(System.currentTimeMillis() - game.getStartTime()); // Set time on win
        }

        games.put(gameId, game);
        return game;
    }

    // Helper: Counts flagged neighbors around a cell
    private int countFlaggedNeighbors(GameBoard game, int row, int col) {
        int flagged = 0;
        int rows = game.getRows();
        int cols = game.getCols();
        for (int rOffset = -1; rOffset <= 1; rOffset++) {
            for (int cOffset = -1; cOffset <= 1; cOffset++) {
                if (rOffset == 0 && cOffset == 0) continue;

                int nRow = row + rOffset;
                int nCol = col + cOffset;

                if (isValidCoord(nRow, nCol, rows, cols)) {
                    if (game.getBoard()[nRow][nCol].isFlagged()) {
                        flagged++;
                    }
                }
            }
        }
        return flagged;
    }

    // Helper: Checks if the game has been won
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

    // Helper: Reveals all mines on the board
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

    // Helper for boundary checks
    private boolean isValidCoord(int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    // ... (rest of helper methods: countFlaggedNeighbors, checkWinCondition, revealAllMines, isValidCoord - NO CHANGE HERE) ...

    // IMPORTANT: Make sure your existing private methods (initializeBoard, placeMines, calculateAdjacentMines)
    // are correctly using the board parameters as shown in the last complete GameService.java,
    // and not referring to a 'game' object implicitly.
    // I've kept the ones that operate on `Cell[][] board` as they were in previous iterations.



}