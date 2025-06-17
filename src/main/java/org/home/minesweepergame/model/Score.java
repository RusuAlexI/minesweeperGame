package org.home.minesweepergame.model;

import jakarta.persistence.*;

import java.time.Instant; // For timestamp
import java.util.Objects;
import java.util.UUID; // For generating unique IDs
@Entity // Mark this class as a JPA entity
@Table(name = "scores") // Specify the database table name
public class Score implements Comparable<Score> {
    @Id // Mark the 'id' field as the primary key
    @GeneratedValue(strategy = GenerationType.UUID) // Generate UUIDs automatically
    private String id;
    @Column(name = "player_name", nullable = false) // Map to a column, enforce not null
    private String playerName;
    @Column(name = "time_taken") // Map to a column
    private Long timeTaken; // Changed to Long to match GameBoard
    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp; // Using Instant for better time handling

    public Score() {
    }

    public Score(String playerName, Long timeTaken, Difficulty difficulty) {
        this.playerName = playerName;
        this.timeTaken = timeTaken;
        this.difficulty = difficulty;
        this.timestamp = Instant.now(); // Set the timestamp when the object is created
    }

    // Getters
    public String getId() { return id; }
    public String getPlayerName() { return playerName; }
    public Long getTimeTaken() { return timeTaken; }
    public Difficulty getDifficulty() { return difficulty; }
    public Instant getTimestamp() { return timestamp; }

    // Setters (Consider removing setId, setTimestamp if these should only be set internally)
    public void setId(String id) { this.id = id; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setTimeTaken(Long timeTaken) { this.timeTaken = timeTaken; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    @Override
    public int compareTo(Score other) {
        if (this.timeTaken == null && other.timeTaken == null) return 0;
        if (this.timeTaken == null) return 1; // Null time is "worse" (longer)
        if (other.timeTaken == null) return -1; // Null time is "worse" (longer)

        int timeCompare = this.timeTaken.compareTo(other.timeTaken);
        if (timeCompare == 0) {
            // Use timestamp for tie-breaking, earlier timestamp wins for same time
            if (this.timestamp == null && other.timestamp == null) return 0;
            if (this.timestamp == null) return 1;
            if (other.timestamp == null) return -1;
            return this.timestamp.compareTo(other.timestamp);
        }
        return timeCompare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return id != null && Objects.equals(id, score.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0; // Or super.hashCode() for transient entities
    }

    @Override
    public String toString() {
        return "Score{" +
                "id='" + id + '\'' +
                ", playerName='" + playerName + '\'' +
                ", timeTaken=" + timeTaken +
                ", difficulty=" + difficulty +
                ", timestamp=" + timestamp +
                '}';
    }
}