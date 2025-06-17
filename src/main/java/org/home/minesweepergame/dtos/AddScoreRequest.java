package org.home.minesweepergame.dtos;

public class AddScoreRequest {
    private String gameId;
    private String playerName;

    // Getters
    public String getGameId() { return gameId; }
    public String getPlayerName() { return playerName; }

    // Setters
    public void setGameId(String gameId) { this.gameId = gameId; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
}