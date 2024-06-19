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
    private String currentGame;
    private List<String> players; // 게임 참가자 목록
    private String currentTurn; // 현재 턴인 플레이어
    private String previousTurn; // 이전 출제자 추적
    private String topic; // 현재 주제
    private Map<String, Integer> scores = new HashMap<>(); // 플레이어별 점수
    private List<String> lastCorrectPlayers = new ArrayList<>(); // 마지막으로 정답을 맞춘 플레이어들
    private boolean isGameOver; // 게임 종료 여부
    private String winner; // 승자
    private String message; // 메시지
    private String liar; // 라이어
    private LiarTopic liarTopic; // 라이어의 주제
    private String bomb; // 폭탄
    private Map<String, Integer> votes = new HashMap<>(); // 플레이어별 투표 수
    private long timeLeft; // 남은 시간 (초)
    private List<String> moves = new ArrayList<>(); // 이동 기록
    private String losingPlayer = ""; // 패배한 플레이어
    private List<String> guessedWords = new ArrayList<>(); // 추측한 단어 목록
    private String Loser = ""; // 패배한 플레이어
    private int currentRound = 0; // 현재 라운드
    private int totalRounds = 5; // 총 라운드 수
    private int choice0 = 0; // 첫 번째 선택지의 투표 수
    private int choice1 = 0; // 두 번째 선택지의 투표 수
    private String[] choices = new String[2]; // 선택지 배열 초기화
    private List<String> completedPlayers = new ArrayList<>(); // 완료된 플레이어 목록 추가
    private List<String> balanceGameVotes = new ArrayList<>();  // 밸런스 게임용 votes 목록
    private BalanceTopic balanceTopic;

    // 생성자: 플레이어 목록을 받아 초기화
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

    // 점수 업데이트: 기본 +1
    public void updateScore(String player) {
        scores.put(player, scores.getOrDefault(player, 0) + 1);
    }

    // 점수 업데이트: 주어진 값 만큼
    public void updateScore(String player, int value) {
        scores.put(player, scores.getOrDefault(player, 0) + value);
    }

    // 투표 추가
    public void addVote(String player) {
        votes.put(player, votes.getOrDefault(player, 0) + 1);
    }


    // 가장 많은 투표를 받은 플레이어 반환
    public void addBalanceGameVote(String player) {
        balanceGameVotes.add(player);
    }


    public String getMostVoted() {
        return votes.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }

    // 게임 상태 초기화
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
        lastCorrectPlayers.clear();
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

    // 점수 초기화
    public void resetScores() {
        scores.clear();
        for (String player : players) {
            scores.put(player, 0);
        }
    }

    // 게임 종료 설정
    public void endGame() {
        isGameOver = true;
    }

    // 이동 처리
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

    // 추측한 단어 추가
    public void addGuessedWord(String word) {
        guessedWords.add(word);
    }

    // 특정 단어가 추측된 단어 목록에 있는지 확인
    public boolean isWordGuessed(String word) {
        return guessedWords.contains(word);
    }

    // 패배한 플레이어 목록 반환
    public List<String> getLosingPlayers() {
        List<String> losingPlayers = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() <= -5) {
                losingPlayers.add(entry.getKey());
            }
        }
        return losingPlayers;
    }

    public void setChoices(String choice0, String choice1) {
        this.choices[0] = choice0;
        this.choices[1] = choice1;

    }

}

