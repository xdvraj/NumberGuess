package com.numberguess.model;

public record GuessOutcome(
        String result,
        String message,
        int attempts,
        int maxAttempts,
        boolean finished,
        boolean won,
        Integer secretReveal
) {
    public static GuessOutcome correct(int attempts, int maxAttempts) {
        return new GuessOutcome(
                "CORRECT",
                "You got it!",
                attempts,
                maxAttempts,
                true,
                true,
                null
        );
    }

    public static GuessOutcome higher(int attempts, int maxAttempts) {
        return new GuessOutcome(
                "HIGHER",
                "Higher than that.",
                attempts,
                maxAttempts,
                false,
                false,
                null
        );
    }

    public static GuessOutcome lower(int attempts, int maxAttempts) {
        return new GuessOutcome(
                "LOWER",
                "Lower than that.",
                attempts,
                maxAttempts,
                false,
                false,
                null
        );
    }

    public static GuessOutcome gameOver(int secret, int attempts, int maxAttempts, boolean lastWasLow) {
        String hint = lastWasLow ? "Your last guess was too low." : "Your last guess was too high.";
        return new GuessOutcome(
                "GAME_OVER",
                "No attempts left. The number was " + secret + ". " + hint,
                attempts,
                maxAttempts,
                true,
                false,
                secret
        );
    }

    public static GuessOutcome outOfRange(int min, int max, int attempts, int maxAttempts) {
        return new GuessOutcome(
                "OUT_OF_RANGE",
                "Pick a number between " + min + " and " + max + " (inclusive).",
                attempts,
                maxAttempts,
                false,
                false,
                null
        );
    }

    public static GuessOutcome alreadyFinished(boolean won, int attempts, int maxAttempts) {
        String msg = won ? "This round is already won. Start a new game." : "This round is over. Start a new game.";
        return new GuessOutcome(
                "ALREADY_FINISHED",
                msg,
                attempts,
                maxAttempts,
                true,
                won,
                null
        );
    }
}
