package com.utbm.loveletter.controller;

import com.utbm.loveletter.dto.GameCreationRequest;
import com.utbm.loveletter.dto.GameStateDTO;
import com.utbm.loveletter.dto.PlayCardRequest;
import com.utbm.loveletter.service.GameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<GameStateDTO> createGame(@RequestBody GameCreationRequest request) {
        if (request.getPlayerNames() == null || request.getPlayerNames().size() < 2 || request.getPlayerNames().size() > 4) {
            return ResponseEntity.badRequest().build();
        }
        GameStateDTO state = gameService.createGame(request.getPlayerNames());
        return ResponseEntity.ok(state);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable String gameId) {
        try {
            GameStateDTO state = gameService.getGameState(gameId);
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{gameId}/draw")
    public ResponseEntity<?> drawCard(@PathVariable String gameId) {
        try {
            GameStateDTO state = gameService.drawCard(gameId);
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{gameId}/play")
    public ResponseEntity<?> playCard(@PathVariable String gameId, @RequestBody PlayCardRequest request) {
        try {
            GameStateDTO state = gameService.playCard(gameId, request);
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{gameId}/next-turn")
    public ResponseEntity<?> nextTurn(@PathVariable String gameId) {
        try {
            GameStateDTO state = gameService.nextTurn(gameId);
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{gameId}/restart")
    public ResponseEntity<?> startNewRound(@PathVariable String gameId) {
        try {
            GameStateDTO state = gameService.startNewRound(gameId);
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
