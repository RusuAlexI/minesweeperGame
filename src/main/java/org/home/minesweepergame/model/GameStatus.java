package org.home.minesweepergame.model;

// Represents the current status of a Minesweeper game.
public enum GameStatus {
    NOT_STARTED, // Game created, but no cells revealed yet
    IN_PROGRESS, // Game actively being played
    WON,         // Player revealed all non-mine cells
    LOST         // Player revealed a mine
}