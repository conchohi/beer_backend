package com.zipbeer.beerbackend.dto.game;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<String> moves = new ArrayList<>();
    private String losingPlayer = "";
    private String currentTurn;
    private List<String> players;

    public GameState(List<String> players) {
        this.players = players;
        if (!players.isEmpty()) {
            this.currentTurn = players.get(0);
        } else {
            this.currentTurn = null;
        }
    }

    public List<String> getMoves() {
        return moves;
    }

    public String getLosingPlayer() {
        return losingPlayer;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void processMove(GameMessage gameMessage) {
        moves.add(gameMessage.getPlayer() + ": " + gameMessage.getNumbers());
        int lastNumber = gameMessage.getNumbers().get(gameMessage.getNumbers().size() - 1);
        if (lastNumber == 31) {
            losingPlayer = gameMessage.getPlayer();
        } else {
            int currentIndex = players.indexOf(currentTurn);
            currentTurn = players.get((currentIndex + 1) % players.size());
        }
    }

    public void reset() {
        moves.clear();
        losingPlayer = "";
        if (!players.isEmpty()) {
            currentTurn = players.get(0);
        } else {
            currentTurn = null;
        }
    }
}
