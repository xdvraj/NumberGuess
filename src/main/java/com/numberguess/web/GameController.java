package com.numberguess.web;

import com.numberguess.model.GameSession;
import com.numberguess.model.GuessOutcome;
import com.numberguess.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/games")
    public Map<String, Object> startGame() {
        GameSession g = gameService.newGame();
        return Map.of(
                "gameId", g.getId().toString(),
                "min", g.getMin(),
                "max", g.getMax(),
                "maxAttempts", g.getMaxAttempts()
        );
    }

    @PostMapping("/games/{gameId}/guesses")
    public ResponseEntity<GuessOutcome> submitGuess(
            @PathVariable UUID gameId,
            @RequestBody Map<String, Integer> body
    ) {
        Integer value = body.get("value");
        if (value == null) {
            return ResponseEntity.badRequest().build();
        }
        return gameService.guess(gameId, value)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
