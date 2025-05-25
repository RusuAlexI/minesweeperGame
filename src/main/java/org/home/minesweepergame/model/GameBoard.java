package org.home.minesweepergame.model;

import java.util.UUID;

public class GameBoard {
    public String id = UUID.randomUUID().toString();
    public int rows;
    public int cols;
    public int mines;
    public Cell[][] board;
    public String status = "IN_PROGRESS"; // or WON/LOST
}
