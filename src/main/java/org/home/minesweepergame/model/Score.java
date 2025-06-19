package org.home.minesweepergame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant; // For timestamp

@Entity // Marks this class as a JPA entity
public class Score {

    @Id // Specifies the primary key
    @GeneratedValue(strategy = GenerationType.UUID) // Generates UUID for ID
    private String id;
    private String gameId; // Reference to the game this score belongs to
    private String playerName;
    private long timeTaken; // Time taken in milliseconds
    @Enumerated(EnumType.STRING) // Store enum as String in DB
    private Difficulty difficulty;
    private long timestamp; // When the score was recorded (epoch milliseconds)

    // Default constructor for JPA
    public Score() {
    }

    // Constructor for creating new Score objects
    public Score(String gameId, String playerName, long timeTaken, Difficulty difficulty) {
        this.gameId = gameId;
        this.playerName = playerName;
        this.timeTaken = timeTaken;
        this.difficulty = difficulty;
        this.timestamp = Instant.now().toEpochMilli(); // Set current time in milliseconds
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id='" + id + '\'' +
                ", gameId='" + gameId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", timeTaken=" + timeTaken +
                ", difficulty=" + difficulty +
                ", timestamp=" + timestamp +
                '}';
    }
}