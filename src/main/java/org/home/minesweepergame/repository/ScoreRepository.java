package org.home.minesweepergame.repository;

import org.home.minesweepergame.model.Difficulty;
import org.home.minesweepergame.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock; // Import Lock
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType; // Import LockModeType

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, String> {

    // Existing method for simple reads (no lock needed)
    List<Score> findTop10ByDifficultyOrderByTimeTakenAsc(Difficulty difficulty);

    // NEW METHOD FOR PESSIMISTIC LOCKING:
    // This method will fetch all scores for a given difficulty and acquire a write lock on them.
    // This ensures no other transaction can modify these scores until this transaction commits.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Score s WHERE s.difficulty = :difficulty ORDER BY s.timeTaken ASC, s.timestamp ASC")
    List<Score> findScoresWithLockByDifficulty(Difficulty difficulty);
}