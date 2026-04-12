package com.numberguess.service;

import com.numberguess.model.GameSession;
import com.numberguess.model.GuessOutcome;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameService {

    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_MAX_ATTEMPTS = 7;

    private final Map<UUID, GameSession> games = new ConcurrentHashMap<>();

    public GameSession newGame() {
        return newGame(DEFAULT_MIN, DEFAULT_MAX, DEFAULT_MAX_ATTEMPTS);
    }

    public GameSession newGame(int min, int max, int maxAttempts) {
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }
        int span = max - min;
        if (span < 1) {
            throw new IllegalArgumentException("Range must allow at least two distinct values.");
        }
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1.");
        }
        UUID id = UUID.randomUUID();
        int secret = ThreadLocalRandom.current().nextInt(min, max + 1);
        GameSession session = new GameSession(id, min, max, secret, maxAttempts);
        games.put(id, session);
        return session;
    }

    public Optional<GuessOutcome> guess(UUID gameId, int value) {
        GameSession session = games.get(gameId);
        if (session == null) {
            return Optional.empty();
        }
        return Optional.of(session.guess(value));
    }
}
