package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.game.GameMessage;
import com.zipbeer.beerbackend.dto.game.GameState;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, GameState> gameRooms = new HashMap<>();
    private final Map<String, Set<String>> usedTopicsMap = new HashMap<>();
    private final Random random = new Random();

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/start/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else {
            gameState.reset();
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        gameState.setCurrentTurn(gameMessage.getPlayers().get(random.nextInt(gameMessage.getPlayers().size())));
        gameState.setTopic(generateTopic("catchMind", roomNo));
        return gameState;
    }

    @MessageMapping("/startCharacterGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startCharacterGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else {
            gameState.reset();
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        gameState.setCurrentTurn(gameMessage.getPlayers().get(random.nextInt(gameMessage.getPlayers().size())));
        gameState.setTopic(generateTopic("character", roomNo));
        return gameState;
    }

    @MessageMapping("/startLiarGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startLiarGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else {
            gameState.reset();
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }

        // 라이어 설정
        String liar = gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()));
        gameState.setLiar(liar);

        // 주제 설정
        String topic = generateTopic("liarGame", roomNo);
        gameState.setTopic(topic);

        // 각 플레이어에게 주제 전달
        for (String player : gameState.getPlayers()) {
            String playerTopic = player.equals(liar) ? "" : topic;
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/topic", new GameMessage(player, playerTopic, null));
        }

        return gameState;
    }

    @MessageMapping("/reset/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState resetGame(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            gameState.reset();
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        return gameState;
    }

    @MessageMapping("/passShoutInSilence/{roomNo}")
    public void passTurnShoutInSilence(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            int currentIndex = gameState.getPlayers().indexOf(gameState.getCurrentTurn());
            String nextTurn = gameState.getPlayers().get((currentIndex + 1) % gameState.getPlayers().size());
            while (nextTurn.equals(gameState.getCurrentTurn())) {
                currentIndex = (currentIndex + 1) % gameState.getPlayers().size();
                nextTurn = gameState.getPlayers().get(currentIndex);
            }
            gameState.setCurrentTurn(nextTurn);
            gameState.setTopic(generateTopic("shoutInSilence", roomNo));
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
    }

    @MessageMapping("/guess/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState processGuess(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null && gameState.getTopic().equalsIgnoreCase(gameMessage.getGuess())) {
            gameState.updateScore(gameMessage.getPlayer()); // 정답 맞춘 사람 점수 증가
            if (gameState.getScores().get(gameMessage.getPlayer()) >= 5) { // 5점 도달 여부 확인
                gameState.setWinner(gameMessage.getPlayer());
                gameState.endGame();
                gameState.resetScores(); // 점수 초기화
            } else {
                gameState.setTopic(generateTopic("catchMind", roomNo));
                gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()))); // 새로운 출제자 랜덤 선택
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", gameMessage.getPlayer());
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
        return gameState;
    }

    @MessageMapping("/guessCharacter/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState processGuessCharacter(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null && gameState.getTopic().equalsIgnoreCase(gameMessage.getGuess())) {
            gameState.updateScore(gameMessage.getPlayer()); // 정답 맞춘 사람 점수 증가
            if (gameState.getScores().get(gameMessage.getPlayer()) >= 5) { // 5점 도달 여부 확인
                gameState.setWinner(gameMessage.getPlayer());
                gameState.endGame();
                gameState.resetScores(); // 점수 초기화
            } else {
                gameState.setTopic(generateTopic("character", roomNo));
                gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()))); // 새로운 출제자 랜덤 선택
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", gameMessage.getPlayer());
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
        return gameState;
    }

    @MessageMapping("/guessCatchMind/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState processGuessCatchMind(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null && gameState.getTopic().equalsIgnoreCase(gameMessage.getGuess())) {
            gameState.updateScore(gameMessage.getPlayer()); // 정답 맞춘 사람 점수 증가
            if (gameState.getScores().get(gameMessage.getPlayer()) >= 5) { // 5점 도달 여부 확인
                gameState.setWinner(gameMessage.getPlayer());
                gameState.endGame();
                gameState.resetScores(); // 점수 초기화
            } else {
                gameState.setTopic(generateTopic("catchMind", roomNo));
                gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()))); // 새로운 출제자 랜덤 선택
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", gameMessage.getPlayer());
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
        return gameState;
    }

    @MessageMapping("/draw/{roomNo}")
    public void receiveDrawing(@DestinationVariable String roomNo, String drawingData) {
        messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/drawing", drawingData);
    }


    @MessageMapping("/selectGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}/select")
    public void selectGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/select", gameMessage);
    }

    private String selectNextTurn(GameState gameState, String previousTurn) {
        List<String> players = gameState.getPlayers();
        String nextTurn = players.get(random.nextInt(players.size()));
        while (nextTurn.equals(previousTurn)) {
            nextTurn = players.get(random.nextInt(players.size()));
        }
        gameState.setPreviousTurn(nextTurn); // 이전 출제자 업데이트
        return nextTurn;
    }

    private String generateTopic(String gameType, String roomNo) {
        String[] catchMindTopics = {"원숭이", "기린", "사과", "김", "배", "수박", "참외", "제비", "소방차", "캐리어", "비","돼지","사슴","키보드","사건","경찰","댄서","고드름","케이크","마늘","나비","잠자리"};
        String[] characterTopics = {"김세정", "김지원", "설현", "수지", "아이유", "윤소희", "조이", "진세연", "채수빈", "카리나", "크리스탈", "해리"};
        String[] shoutInSilenceTopics = {"원숭이", "기린", "사과", "김", "배", "수박", "참외", "제비", "소방차", "캐리어", "비", "돼지", "사슴", "키보드", "사건", "경찰", "댄서", "고드름", "케이크", "마늘", "나비", "잠자리"};
//        String[] liarGameTopics = {"해변", "도서관", "영화관", "공원", "놀이공원", "카페", "서점", "박물관", "식당", "학교"};

        String[] topics;

        switch (gameType) {
            case "character":
                topics = characterTopics;
                break;
//            case "shoutInSilence":
//                topics = shoutInSilenceTopics;
//                break;
//            case "liarGame":
//                topics = liarGameTopics;
//                break;
            case "catchMind":
            default:
                topics = catchMindTopics;
                break;
        }

        Set<String> usedTopics = usedTopicsMap.computeIfAbsent(roomNo, k -> new HashSet<>());
        String topic;
        do {
            topic = topics[random.nextInt(topics.length)];
        } while (usedTopics.contains(topic));
        usedTopics.add(topic);
        return topic;
    }
}
