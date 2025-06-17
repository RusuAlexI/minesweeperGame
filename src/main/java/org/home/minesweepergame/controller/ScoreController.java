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

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "http://localhost:4200") //  <----  CRUCIAL:  Enable CORS for your Angular app
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/add")
    public ResponseEntity<Score> addScore(@RequestBody AddScoreRequest request) {
        try {
            Score newScore = scoreService.addScore(request.getGameId(), request.getPlayerName());
            return new ResponseEntity<>(newScore, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/top/{difficulty}")
    public ResponseEntity<List<Score>> getTopScores(@PathVariable Difficulty difficulty) {
        List<Score> topScores = scoreService.getTopScores(difficulty);
        return new ResponseEntity<>(topScores, HttpStatus.OK);
    }
}