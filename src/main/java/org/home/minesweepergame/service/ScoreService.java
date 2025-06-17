package org.home.minesweepergame.service;

import org.home.minesweepergame.model.Difficulty;
import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.model.GameStatus;
import org.home.minesweepergame.model.Score;
import org.home.minesweepergame.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Ensure this is imported


import java.util.Comparator; // Import Comparator
import java.util.List;
import java.util.stream.Collectors; // Import Collectors
@Service
public class ScoreService {

    private final GameService gameService;
    private final ScoreRepository scoreRepository;
    private static final int MAX_SCORES_PER_DIFFICULTY = 10;

    @Autowired
    public ScoreService(GameService gameService, ScoreRepository scoreRepository) {
        this.gameService = gameService;
        this.scoreRepository = scoreRepository;
    }

    /**
     * Adds a new score if it's a valid win and qualifies for the top list.
     * @param gameId The ID of the game that was won.
     * @param playerName The name of the player.
     * @return The Score object added to the list, or null if it didn't qualify.
     */
    @Transactional // Ensures the entire method runs within a single transaction
    public Score addScore(String gameId, String playerName) {
        GameBoard game = gameService.getGame(gameId);
        if (game == null || game.getStatus() != GameStatus.WON || game.getTimeTaken() == null) {
            throw new IllegalArgumentException("Game not found, not won, or time not recorded for game ID: " + gameId);
        }

        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Anonymous";
        }

        Score newScore = new Score(playerName, game.getTimeTaken(), game.getDifficulty());

        // 1. Get current top scores for this difficulty
        // This list is already ordered by timeTakenAsc due to the repository method name
        List<Score> currentTopScores = scoreRepository.findTop10ByDifficultyOrderByTimeTakenAsc(game.getDifficulty());

        // 2. Check if the new score makes the top MAX_SCORES_PER_DIFFICULTY
        if (currentTopScores.size() < MAX_SCORES_PER_DIFFICULTY) {
            // List is not full, simply save the new score
            return scoreRepository.save(newScore);
        } else {
            // List is full, compare with the current worst score (last element in the sorted list)
            Score worstScore = currentTopScores.get(currentTopScores.size() - 1);

            // If the new score is better than the current worst score (lower timeTaken)
            if (newScore.compareTo(worstScore) < 0) {
                // Delete the existing worst score from the database
                scoreRepository.delete(worstScore);
                // Save the new (better) score to the database
                return scoreRepository.save(newScore);
            } else {
                // New score is not good enough to make the top MAX_SCORES_PER_DIFFICULTY
                return null;
            }
        }
    }

    /**
     * Retrieves the top scores for a given difficulty from the database.
     * @param difficulty The difficulty level to retrieve scores for.
     * @return A list of top scores.
     */
    public List<Score> getTopScores(Difficulty difficulty) {
        // The repository method automatically handles the top 10 and sorting
        return scoreRepository.findTop10ByDifficultyOrderByTimeTakenAsc(difficulty);
    }
}