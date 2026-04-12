(function () {
    const intro = document.getElementById("intro");
    const attemptsEl = document.getElementById("attempts");
    const form = document.getElementById("guess-form");
    const input = document.getElementById("guess-input");
    const guessBtn = document.getElementById("guess-btn");
    const feedback = document.getElementById("feedback");
    const newGameBtn = document.getElementById("new-game-btn");

    let state = {
        gameId: null,
        min: 1,
        max: 100,
        maxAttempts: 7,
        finished: false,
    };

    function setFeedback(text, className) {
        feedback.textContent = text;
        feedback.className = "feedback" + (className ? " " + className : "");
    }

    function setPlaying(playing) {
        input.disabled = !playing;
        guessBtn.disabled = !playing;
        input.min = state.min;
        input.max = state.max;
        if (playing) {
            input.value = "";
            input.focus();
        }
    }

    function updateAttempts(current, max) {
        const left = max - current;
        attemptsEl.hidden = false;
        attemptsEl.textContent =
            left > 0
                ? `Attempts used: ${current} of ${max} (${left} left).`
                : `Attempts used: ${current} of ${max}.`;
    }

    async function startGame() {
        setFeedback("");
        intro.textContent = "Starting a new round…";
        attemptsEl.hidden = true;
        setPlaying(false);
        newGameBtn.disabled = true;

        try {
            const res = await fetch("/api/games", { method: "POST" });
            if (!res.ok) throw new Error("Could not start game");
            const data = await res.json();
            state = {
                gameId: data.gameId,
                min: data.min,
                max: data.max,
                maxAttempts: data.maxAttempts,
                finished: false,
            };
            intro.textContent = `Guess a number from ${state.min} to ${state.max}. You have ${state.maxAttempts} tries.`;
            updateAttempts(0, state.maxAttempts);
            setPlaying(true);
        } catch (e) {
            intro.textContent = "";
            setFeedback("Could not reach the server. Is the app running?", "bad");
        } finally {
            newGameBtn.disabled = false;
        }
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        if (!state.gameId || state.finished) return;

        const value = parseInt(input.value, 10);
        if (Number.isNaN(value)) return;

        guessBtn.disabled = true;
        try {
            const res = await fetch(`/api/games/${state.gameId}/guesses`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ value }),
            });
            if (res.status === 404) {
                setFeedback("This game expired. Start a new game.", "bad");
                state.finished = true;
                setPlaying(false);
                return;
            }
            if (!res.ok) {
                setFeedback("Something went wrong. Try again.", "bad");
                guessBtn.disabled = false;
                return;
            }
            const outcome = await res.json();
            updateAttempts(outcome.attempts, outcome.maxAttempts);

            const r = outcome.result;
            if (r === "CORRECT") {
                setFeedback(outcome.message, "correct");
                state.finished = true;
                setPlaying(false);
            } else if (r === "GAME_OVER" || r === "ALREADY_FINISHED") {
                setFeedback(outcome.message, "over");
                state.finished = true;
                setPlaying(false);
            } else if (r === "OUT_OF_RANGE") {
                setFeedback(outcome.message, "bad");
                guessBtn.disabled = false;
            } else if (r === "HIGHER") {
                setFeedback(outcome.message, "higher");
                guessBtn.disabled = false;
            } else if (r === "LOWER") {
                setFeedback(outcome.message, "lower");
                guessBtn.disabled = false;
            } else {
                setFeedback(outcome.message || "Unknown response.", "bad");
                guessBtn.disabled = false;
            }
        } catch (err) {
            setFeedback("Network error. Check your connection.", "bad");
            guessBtn.disabled = false;
        }
    });

    newGameBtn.addEventListener("click", startGame);

    startGame();
})();
