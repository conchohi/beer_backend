package com.zipbeer.beerbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private Map<String, Integer> scores = new HashMap<>();
    private boolean isGameOver;
    private String winner;
    private String message;
    private String liar;
    private LiarTopic liarTopic;
    private BalanceTopic balanceTopic;
    private String bomb;
    private Map<String, Integer> votes = new HashMap<>();
    private int timeLeft; // 타이머 필드 추가
    private List<String> moves = new ArrayList<>();
    private String losingPlayer = "";
    private int currentRound = 0; // 현재 라운드
    private int totalRounds = 5; // 총 라운드 수
    private int choice0 = 0; // 첫 번째 선택지의 투표 수
    private int choice1 = 0; // 두 번째 선택지의 투표 수
    private String[] choices = new String[2]; // 선택지 배열 초기화
    private List<String> completedPlayers = new ArrayList<>(); // 완료된 플레이어 목록 추가
    private List<String> balanceGameVotes = new ArrayList<>();  // 밸런스 게임용 votes 목록

    public GameState(List<String> players) {
        this.players = players;
        for (String player : players) {
            scores.put(player, 0);
            votes.put(player, 0);
        }
        if (!players.isEmpty()) {
            this.currentTurn = players.get(0);
        } else {
            this.currentTurn = null;
        }
        this.timeLeft = 180; // 초기 타이머 설정
        this.currentRound = 1; // 게임 시작 시 첫 라운드 설정
        this.totalRounds = 5; // 총 라운드 수
        this.choice0 = 0; // 첫 번째 선택지의 초기 투표 수
        this.choice1 = 0; // 두 번째 선택지의 초기 투표 수
        this.choices = new String[2]; // 선택지 배열 초기화

    }

    public void updateScore(String player) {
        scores.put(player, scores.get(player) + 1);
    }

    public void addVote(String player) {
        votes.put(player, votes.getOrDefault(player, 0) + 1);
    }

    public void addBalanceGameVote(String player) {
        balanceGameVotes.add(player);
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
        timeLeft = 180; // 타이머 초기화
        for (String player : players) {
            scores.put(player, 0);
            votes.put(player, 0);
        }
        moves.clear();
        losingPlayer = "";
        if (!players.isEmpty()) {
            currentTurn = players.get(0);
        } else {
            currentTurn = null;
        }
        choices = new String[2];
        this.currentRound = 1; // 라운드 초기화
        this.choice0 = 0; // 첫 번째 선택지의 초기화
        this.choice1 = 0; // 두 번째 선택지의 초기화
        completedPlayers.clear(); // 완료된 플레이어 목록 초기화
        balanceGameVotes.clear(); // 밸런스 게임용 votes 초기화
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
    public void processMove(GameMessage gameMessage) {
        moves.add(gameMessage.getPlayer() + ": " + gameMessage.getNumbers());
        int lastNumber = gameMessage.getNumbers().get(gameMessage.getNumbers().size() - 1);
        if (lastNumber >= 31) {
            losingPlayer = gameMessage.getPlayer();
        } else {
            int currentIndex = players.indexOf(currentTurn);
            currentTurn = players.get((currentIndex + 1) % players.size());
        }
    }

    public void setChoices(String choice0, String choice1) {
        this.choices[0] = choice0;
        this.choices[1] = choice1;
    }
}
