package org.home.minesweepergame.controller;

import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.dtos.CellActionRequest; // Make sure this import is present
import org.home.minesweepergame.dtos.GameStartRequest; // Make sure this import is present
import org.home.minesweepergame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:4200") // IMPORTANT: Ensure this is still here!
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public ResponseEntity<GameBoard> startGame(@RequestBody GameStartRequest request) {
        try {
            // Modified to pass Difficulty directly
            GameBoard newGame = gameService.startGame(request.getDifficulty());
            return new ResponseEntity<>(newGame, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{gameId}/reveal")
    public ResponseEntity<GameBoard> revealCell(
            @PathVariable String gameId,
            @RequestBody CellActionRequest request) { // <--- ADD @RequestBody HERE
        try {
            GameBoard updatedGame = gameService.revealCell(gameId, request.getRow(), request.getCol());
            if (updatedGame == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedGame, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{gameId}/flag")
    public ResponseEntity<GameBoard> flagCell(
            @PathVariable String gameId,
            @RequestBody CellActionRequest request) { // <--- ADD @RequestBody HERE
        try {
            GameBoard updatedGame = gameService.flagCell(gameId, request.getRow(), request.getCol());
            if (updatedGame == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedGame, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameBoard> getGame(@PathVariable String gameId) {
        GameBoard game = gameService.getGame(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PutMapping("/{gameId}/chord") // <--- NEW Endpoint
    public ResponseEntity<GameBoard> chordClick(
            @PathVariable String gameId,
            @RequestBody CellActionRequest request) {
        try {
            GameBoard updatedGame = gameService.chordClick(gameId, request.getRow(), request.getCol()); // <--- NEW Service Call
            if (updatedGame == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedGame, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}