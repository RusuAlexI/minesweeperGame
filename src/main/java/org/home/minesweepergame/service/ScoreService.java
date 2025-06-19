package org.home.minesweepergame.service;

import org.home.minesweepergame.model.Difficulty;
import org.home.minesweepergame.model.GameBoard; // To get game details like difficulty and timeTaken
import org.home.minesweepergame.model.Score;
import org.home.minesweepergame.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final GameService gameService; // Inject GameService to get game details

    @Autowired
    public ScoreService(ScoreRepository scoreRepository, GameService gameService) {
        this.scoreRepository = scoreRepository;
        this.gameService = gameService;
    }

    /**
     * Saves a new score for a completed game.
     * Fetches game details (difficulty, timeTaken) from the GameService.
     * @param gameId The ID of the completed game.
     * @param playerName The name of the player.
     * @return The saved Score object.
     * @throws IllegalArgumentException if the game is not found or not in a 'WON' state.
     */
    public Score addScore(String gameId, String playerName) {
        // Retrieve the game details to get difficulty and timeTaken
        GameBoard game = gameService.getGame(gameId); // Get game from activeGames map in GameService

        if (game == null) {
            throw new IllegalArgumentException("Game with ID " + gameId + " not found.");
        }
        if (game.getStatus() != org.home.minesweepergame.model.GameStatus.WON) {
            throw new IllegalArgumentException("Game with ID " + gameId + " is not in 'WON' state. Score cannot be saved.");
        }

        // Create a new Score object
        Score newScore = new Score(
                game.getGameId(),
                playerName,
                game.getTimeTaken(), // Use timeTaken from the completed game
                game.getDifficulty()
        );

        // Save the score to the database
        return scoreRepository.save(newScore);
    }

    /**
     * Retrieves the top scores for a given difficulty.
     * @param difficulty The difficulty level to fetch scores for.
     * @return A list of top scores.
     */
    public List<Score> getTopScores(Difficulty difficulty) {
        return scoreRepository.findTop10ByDifficultyOrderByTimeTakenAscTimestampAsc(difficulty);
    }
}