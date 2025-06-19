package org.home.minesweepergame.controller;

import org.home.minesweepergame.dtos.CellActionRequest;
import org.home.minesweepergame.dtos.GameCreationRequest;
import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.service.GameService;
import org.home.minesweepergame.service.ScoreService; // Will be used later for score submission
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:4200") // IMPORTANT: Ensure this matches your frontend's origin!
public class GameController {

    private final GameService gameService;
    private final ScoreService scoreService; // Inject ScoreService for future score handling

    @Autowired
    public GameController(GameService gameService, ScoreService scoreService) {
        this.gameService = gameService;
        this.scoreService = scoreService;
    }

    /**
     * Endpoint to create a new game.
     * It can accept either a predefined difficulty or custom rows, columns, and mines.
     *
     * @param request GameCreationRequest containing game parameters.
     * @return ResponseEntity with the newly created GameBoard or a BAD_REQUEST status.
     */
    @PostMapping("/create")
    public ResponseEntity<GameBoard> createGame(@RequestBody GameCreationRequest request) {
        try {
            GameBoard newGame = gameService.createGame(request);
            return new ResponseEntity<>(newGame, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle invalid game creation requests (e.g., bad custom dimensions)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint to reveal a cell at given coordinates for a specific game.
     *
     * @param gameId The ID of the game.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return ResponseEntity with the updated GameBoard or a NOT_FOUND/BAD_REQUEST status.
     */
    @PostMapping("/{gameId}/reveal/{row}/{col}")
    public ResponseEntity<GameBoard> revealCell(
            @PathVariable String gameId,
            @PathVariable int row,
            @PathVariable int col) {
        try {
            // GameService will handle the reveal logic, including starting the timer and placing mines on first click
            GameBoard updatedGame = gameService.revealCell(gameId, row, col);
            if (updatedGame == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Game not found or already ended
            }
            return new ResponseEntity<>(updatedGame, HttpStatus.OK);
        } catch (Exception e) { // Catching generic exception for unexpected errors during reveal
            System.err.println("Error revealing cell: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to flag/unflag a cell at given coordinates for a specific game.
     *
     * @param gameId The ID of the game.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return ResponseEntity with the updated GameBoard or a NOT_FOUND/BAD_REQUEST status.
     */
    @PostMapping("/{gameId}/flag/{row}/{col}")
    public ResponseEntity<GameBoard> flagCell(
            @PathVariable String gameId,
            @PathVariable int row,
            @PathVariable int col) {
        try {
            GameBoard updatedGame = gameService.flagCell(gameId, row, col);
            if (updatedGame == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedGame, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error flagging cell: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to perform a "chord" click operation on a revealed cell.
     * This reveals unflagged neighbors if the number of flagged neighbors matches adjacent mines.
     *
     * @param gameId The ID of the game.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @return ResponseEntity with the updated GameBoard or a NOT_FOUND/BAD_REQUEST status.
     */
    @PostMapping("/{gameId}/chord/{row}/{col}")
    public ResponseEntity<GameBoard> chordClick(
            @PathVariable String gameId,
            @PathVariable int row,
            @PathVariable int col) {
        try {
            GameBoard updatedGame = gameService.chordClick(gameId, row, col);
            if (updatedGame == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedGame, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error during chord click: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to retrieve the current state of a specific game board.
     *
     * @param gameId The ID of the game.
     * @return ResponseEntity with the GameBoard or a NOT_FOUND status.
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameBoard> getGame(@PathVariable String gameId) {
        GameBoard game = gameService.getGame(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }
}