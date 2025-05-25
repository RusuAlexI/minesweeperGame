package org.home.minesweepergame.service;

import org.home.minesweepergame.model.Cell;
import org.home.minesweepergame.model.GameBoard;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final Map<String, GameBoard> games = new HashMap<>();

    public GameBoard newGame(int rows, int cols, int mines) {
        GameBoard game = new GameBoard();
        game.rows = rows;
        game.cols = cols;
        game.mines = mines;
        game.board = new Cell[rows][cols];

        // Initialize cells
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                game.board[i][j] = new Cell();

        // Place mines randomly
        placeMines(game);
        updateAdjacency(game);

        games.put(game.id, game);
        return game;
    }

    private void placeMines(GameBoard game) {
        Random rand = new Random();
        int placed = 0;
        while (placed < game.mines) {
            int r = rand.nextInt(game.rows);
            int c = rand.nextInt(game.cols);
            if (!game.board[r][c].isMine) {
                game.board[r][c].isMine = true;
                placed++;
            }
        }
    }

    private void updateAdjacency(GameBoard game) {
        for (int i = 0; i < game.rows; i++) {
            for (int j = 0; j < game.cols; j++) {
                if (game.board[i][j].isMine) continue;
                int count = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = i + dr, nc = j + dc;
                        if (nr >= 0 && nr < game.rows && nc >= 0 && nc < game.cols) {
                            if (game.board[nr][nc].isMine) count++;
                        }
                    }
                }
                game.board[i][j].adjacentMines = count;
            }
        }
    }

    public GameBoard getGame(String id) {
        return games.get(id);
    }

    public GameBoard reveal(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.status != "IN_PROGRESS") return game;

        Cell cell = game.board[row][col];
        if (cell.isFlagged || cell.isRevealed) return game;

        cell.isRevealed = true;

        if (cell.isMine) {
            game.status = "LOST";
            return game;
        }

        if (cell.adjacentMines == 0) {
            floodReveal(game, row, col);
        }

        if (checkWin(game)) {
            game.status = "WON";
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
                if (nr >= 0 && nr < game.rows && nc >= 0 && nc < game.cols) {
                    Cell neighbor = game.board[nr][nc];
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
        for (int i = 0; i < game.rows; i++) {
            for (int j = 0; j < game.cols; j++) {
                Cell cell = game.board[i][j];
                if (!cell.isMine && !cell.isRevealed) return false;
            }
        }
        return true;
    }
    public GameBoard toggleFlag(String gameId, int row, int col) {
        GameBoard game = games.get(gameId);
        if (game == null || game.status != "IN_PROGRESS") return game;

        Cell cell = game.board[row][col];
        if (!cell.isRevealed) {
            cell.isFlagged = !cell.isFlagged;
        }
        return game;
    }

}
