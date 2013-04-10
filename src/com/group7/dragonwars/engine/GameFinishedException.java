package com.group7.dragonwars.engine;

public class GameFinishedException extends Exception {
    private Player winner;

    public GameFinishedException(Player p) {
        this.winner = p;
    }

    public Player getWinner() {
        return winner;
    }
}
