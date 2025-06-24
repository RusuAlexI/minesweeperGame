package org.home.minesweepergame.controller;

import org.home.minesweepergame.dtos.AddScoreRequest;
import org.home.minesweepergame.model.Difficulty;
import org.home.minesweepergame.model.Score;
import org.home.minesweepergame.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/game/scores") // Base path for score-related endpoints
@CrossOrigin(origins = {"http://localhost:4200", "https://minesweeper-frontend-9th2.onrender.com"}) // <-- MODIFY THIS LINE
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    /**
     * Endpoint to add a new score.
     * Expects gameId and playerName in the request body.
     */
    @PostMapping("/add")
    public ResponseEntity<Score> addScore(@RequestBody AddScoreRequest scoreRequest) {
        try {
            Score savedScore = scoreService.addScore(scoreRequest.getGameId(), scoreRequest.getPlayerName());
            return new ResponseEntity<>(savedScore, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    /**
     * Endpoint to get top scores for a specific difficulty.
     */
    @GetMapping("/{difficulty}")
    public ResponseEntity<List<Score>> getTopScores(@PathVariable Difficulty difficulty) {
        try {
            List<Score> topScores = scoreService.getTopScores(difficulty);
            return new ResponseEntity<>(topScores, HttpStatus.OK); // 200 OK
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}