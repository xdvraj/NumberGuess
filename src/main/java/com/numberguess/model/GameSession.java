package com.numberguess.model;

import java.util.UUID;

public final class GameSession {

    private final UUID id;
    private final int min;
    private final int max;
    private final int secret;
    private final int maxAttempts;
    private int attempts;
    private boolean finished;
    private boolean won;

    public GameSession(UUID id, int min, int max, int secret, int maxAttempts) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.secret = secret;
        this.maxAttempts = maxAttempts;
    }

    public UUID getId() {
        return id;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public GuessOutcome guess(int value) {
        if (finished) {
            return GuessOutcome.alreadyFinished(won, attempts, maxAttempts);
        }
        if (value < min || value > max) {
            return GuessOutcome.outOfRange(min, max, attempts, maxAttempts);
        }
        attempts++;
        if (value == secret) {
            finished = true;
            won = true;
            return GuessOutcome.correct(attempts, maxAttempts);
        }
        if (attempts >= maxAttempts) {
            finished = true;
            won = false;
            return GuessOutcome.gameOver(secret, attempts, maxAttempts, value < secret);
        }
        if (value < secret) {
            return GuessOutcome.higher(attempts, maxAttempts);
        }
        return GuessOutcome.lower(attempts, maxAttempts);
    }
}
