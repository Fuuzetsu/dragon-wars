package com.group7.dragonwars.engine;

public final class GameFinishedException extends Exception {
    private Player winner;

    public GameFinishedException(final Player p) {
        this.winner = p;
    }

    public Player getWinner() {
        return winner;
    }
}
