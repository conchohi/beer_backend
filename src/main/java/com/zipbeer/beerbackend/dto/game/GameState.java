package com.zipbeer.beerbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameState {
    private List<String> players;
    private String currentTurn;
    private String previousTurn; // 이전 출제자 추적
    private String topic;
    private LiarTopic liarTopic;
    private Map<String, Integer> scores = new HashMap<>();
    private boolean isGameOver;
    private String winner;
    private String message;
    private String liar;
    private String voteFor;
    private String bomb;
    private int leftTime;

    private Map<String, Integer> votes = new HashMap<>();

    public GameState(List<String> players) {
        this.players = players;
        for (String player : players) {
            scores.put(player, 0);
            votes.put(player, 0);
        }
    }

    public void updateScore(String player) {
        scores.put(player, scores.get(player) + 1);
    }

    public void addVote(String player) {
        votes.put(player, votes.getOrDefault(player, 0) + 1);
    }

    public String getMostVoted() {
        return votes.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    public void reset() {
        currentTurn = players.get(0);
        previousTurn = null;
        topic = "";
        scores.clear();
        isGameOver = false;
        winner = "";
        liar = "";
        message = "";
        votes.clear();
        for (String player : players) {
            scores.put(player, 0);
            votes.put(player, 0);
        }
    }

    public void resetScores() {
        scores.clear();
        for (String player : players) {
            scores.put(player, 0);
        }
    }

    public void endGame() {
        isGameOver = true;
    }
}
