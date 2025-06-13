package org.home.minesweepergame.service;

import org.home.minesweepergame.model.Cell;
import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.model.GameStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final Map<String, GameBoard> games = new HashMap<>();

    public GameBoard startGame(int rows, int cols, int mines) {
        // ... parameter validation ...

        String gameId = UUID.randomUUID().toString(); // This generates the ID
        Cell[][] board = initializeBoard(rows, cols);
        placeMines(board, mines);
        calculateAdjacentMines(board);

        GameBoard game = new GameBoard(
                gameId,                 // <--- This should be assigned to ID
                rows,
                cols,
                mines,
                board,
                GameStatus.IN_PROGRESS, // <--- This should be assigned to status
                System.currentTimeMillis() // <--- This should be assigned to startTime
        );
        games.put(gameId, game); // Store the game
        return game; // Return the fully initialized game object
    }

    private void placeMines(GameBoard game) {
        Random rand = new Random();
        int placed = 0;
        while (placed < game.getMines()) {
            int r = rand.nextInt(game.getRows());
            int c = rand.nextInt(game.getCols());
            if (!game.getBoard()[r][c].isMine) {
                game.getBoard()[r][c].isMine = true;
                placed++;
            }
        }
    }

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

    private void updateAdjacency(GameBoard game) {
        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                if (game.getBoard()[i][j].isMine) continue;
                int count = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = i + dr, nc = j + dc;
                        if (nr >= 0 && nr < game.getRows() && nc >= 0 && nc < game.getCols()) {
                            if (game.getBoard()[nr][nc].isMine) count++;
                        }
                    }
                }
                game.getBoard()[i][j].adjacentMines = count;
            }
        }
    }

    public GameBoard getGame(String id) {
        return games.get(id);
    }

    public GameBoard reveal(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus().name() != "IN_PROGRESS") return game;

        Cell cell = game.getBoard()[row][col];
        if (cell.isFlagged || cell.isRevealed) return game;

        cell.isRevealed = true;

        if (cell.isMine) {
            game.setStatus(GameStatus.LOST);
            return game;
        }

        if (cell.adjacentMines == 0) {
            floodReveal(game, row, col);
        }

        if (checkWin(game)) {
            game.setStatus(GameStatus.WON);
        }

        return game;
    }

    private void floodReveal(GameBoard game, int row, int col) {
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{row, col});

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int r = pos[0], c = pos[1];

            for (int i = 0; i < 8; i++) {
                int nr = r + dr[i], nc = c + dc[i];
                if (nr >= 0 && nr < game.getRows() && nc >= 0 && nc < game.getCols()) {
                    Cell neighbor = game.getBoard()[nr][nc];
                    if (!neighbor.isRevealed && !neighbor.isMine && !neighbor.isFlagged) {
                        neighbor.isRevealed = true;
                        if (neighbor.adjacentMines == 0) {
                            queue.add(new int[]{nr, nc});
                        }
                    }
                }
            }
        }
    }

    private boolean checkWin(GameBoard game) {
        for (int i = 0; i < game.getRows(); i++) {
            for (int j = 0; j < game.getCols(); j++) {
                Cell cell = game.getBoard()[i][j];
                if (!cell.isMine && !cell.isRevealed) return false;
            }
        }
        return true;
    }
    public GameBoard toggleFlag(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus().name() != "IN_PROGRESS") return game;

        Cell cell = game.getBoard()[row][col];
        if (!cell.isRevealed) {
            cell.isFlagged = !cell.isFlagged;
        }
        return game;
    }

    public GameBoard revealCell(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return null; // Game not found or not in progress
        }

        if (!isValidCell(game, row, col)) {
            throw new IllegalArgumentException("Invalid cell coordinates.");
        }

        Cell cell = game.getBoard()[row][col];

        if (cell.isRevealed() || cell.isFlagged()) {
            return game; // Already revealed or flagged, do nothing
        }

        cell.setRevealed(true);

        if (cell.isMine()) {
            game.setStatus(GameStatus.LOST);
            revealAllMines(game); // Show all mines on loss
        } else if (cell.getAdjacentMines() == 0) {
            // Recursive reveal for empty cells
            revealAdjacentEmptyCells(game, row, col);
        }

        checkWinCondition(game);
        return game;
    }

    public GameBoard flagCell(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.getStatus() != GameStatus.IN_PROGRESS) {
            return null;
        }

        if (!isValidCell(game, row, col)) {
            throw new IllegalArgumentException("Invalid cell coordinates.");
        }

        Cell cell = game.getBoard()[row][col];
        if (!cell.isRevealed()) { // Only allow flagging unrevealed cells
            cell.setFlagged(!cell.isFlagged()); // Toggle flag
        }
        return game;
    }

    private Cell[][] initializeBoard(int rows, int cols) {
        Cell[][] board = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell(false, false, false, 0);
            }
        }
        return board;
    }

    private void calculateAdjacentMines(Cell[][] board) {
        int rows = board.length;
        int cols = board[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!board[r][c].isMine()) {
                    int count = 0;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            if (dr == 0 && dc == 0) continue; // Skip current cell

                            int nr = r + dr;
                            int nc = c + dc;

                            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc].isMine()) {
                                count++;
                            }
                        }
                    }
                    board[r][c].setAdjacentMines(count);
                }
            }
        }
    }

    // Recursive reveal for 0-adjacent mine cells
    private void revealAdjacentEmptyCells(GameBoard game, int row, int col) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{row, col});
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

                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
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

    private void revealAllMines(GameBoard game) {
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                Cell cell = game.getBoard()[r][c];
                if (cell.isMine()) {
                    cell.setRevealed(true); // Reveal all mines when game is lost
                }
            }
        }
    }

    private void checkWinCondition(GameBoard game) {
        int revealedNonMineCells = 0;
        int totalNonMineCells = (game.getRows() * game.getCols()) - game.getMines();

        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                Cell cell = game.getBoard()[r][c];
                if (cell.isRevealed() && !cell.isMine()) {
                    revealedNonMineCells++;
                }
            }
        }

        if (revealedNonMineCells == totalNonMineCells) {
            game.setStatus(GameStatus.WON);
        }
    }

    private boolean isValidCell(GameBoard game, int row, int col) {
        return row >= 0 && row < game.getRows() && col >= 0 && col < game.getCols();
    }

}
