package org.home.minesweepergame.controller;

import org.home.minesweepergame.model.GameBoard;
import org.home.minesweepergame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private GameService gameService;

    @PostMapping("/new")
    public GameBoard newGame(@RequestParam(defaultValue = "10") int rows,
                             @RequestParam(defaultValue = "10") int cols,
                             @RequestParam(defaultValue = "10") int mines) {
        return gameService.newGame(rows, cols, mines);
    }

    @GetMapping("/{id}")
    public GameBoard getGame(@PathVariable String id) {
        return gameService.getGame(id);
    }

    @PostMapping("/{id}/reveal")
    public GameBoard reveal(@PathVariable String id,
                            @RequestParam int row,
                            @RequestParam int col) {
        return gameService.reveal(id, row, col);
    }

    @PostMapping("/{id}/flag")
    public GameBoard flag(@PathVariable String id,
                          @RequestParam int row,
                          @RequestParam int col) {
        return gameService.toggleFlag(id, row, col);
    }

}
