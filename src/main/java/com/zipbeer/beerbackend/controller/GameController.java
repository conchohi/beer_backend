package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.game.GameMessage;
import com.zipbeer.beerbackend.dto.game.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class GameController {

    private Map<Long, List<String>> roomMoves = new HashMap<>();
    private Map<Long, Integer> roomCurrentNumber = new HashMap<>();
    private Map<Long, List<String>> roomPlayers = new HashMap<>();
    private Map<Long, Integer> roomCurrentPlayerIndex = new HashMap<>();

    @MessageMapping("/game/join/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState join(@PathVariable long roomNo) {
        return getCurrentGameState(roomNo);
    }

    @MessageMapping("/game/start/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startGame(@PathVariable long roomNo, @Payload Map<String, List<String>> payload) {
        System.out.println("Received payload: " + payload); // 로깅 추가
        List<String> players = payload.get("players");
        if (players == null) {
            throw new IllegalArgumentException("Players list cannot be null");
        }
        roomPlayers.put(roomNo, players);
        roomCurrentNumber.put(roomNo, 0);
        roomMoves.put(roomNo, new ArrayList<>());
        roomCurrentPlayerIndex.put(roomNo, 0);
        return getCurrentGameState(roomNo);
    }

    @MessageMapping("/game/move/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState sendMove(@PathVariable long roomNo, @Payload GameMessage message) {
        if (message == null || message.getPlayer() == null || message.getNumbers() == null || message.getNumbers().isEmpty()) {
            throw new IllegalArgumentException("Invalid game message");
        }

        if (!roomCurrentNumber.containsKey(roomNo)) {
            roomCurrentNumber.put(roomNo, 0);
        }
        if (!roomMoves.containsKey(roomNo)) {
            roomMoves.put(roomNo, new ArrayList<>());
        }
        if (!roomCurrentPlayerIndex.containsKey(roomNo)) {
            roomCurrentPlayerIndex.put(roomNo, 0);
        }

        List<Integer> numbers = message.getNumbers();
        int lastNumber = numbers.get(numbers.size() - 1);

        if (lastNumber <= roomCurrentNumber.get(roomNo) || lastNumber > roomCurrentNumber.get(roomNo) + 3) {
            throw new IllegalArgumentException("Invalid move");
        }

        roomCurrentNumber.put(roomNo, lastNumber);
        roomMoves.get(roomNo).add(message.getPlayer() + ": " + numbers.toString());

        GameState gameState = getCurrentGameState(roomNo);

        if (roomCurrentNumber.get(roomNo) >= 31) {
            gameState.setLosingPlayer(message.getPlayer());
            resetGame(roomNo);
            return gameState;
        }

        int currentPlayerIndex = roomCurrentPlayerIndex.get(roomNo);
        currentPlayerIndex = (currentPlayerIndex + 1) % roomPlayers.get(roomNo).size();
        roomCurrentPlayerIndex.put(roomNo, currentPlayerIndex);
        gameState.setCurrentTurn(roomPlayers.get(roomNo).get(currentPlayerIndex));

        return gameState;
    }

    @MessageMapping("/game/reset/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState resetGame(@PathVariable long roomNo) {
        roomMoves.put(roomNo, new ArrayList<>());
        roomCurrentNumber.put(roomNo, 0);
        roomCurrentPlayerIndex.put(roomNo, 0);
        roomPlayers.put(roomNo, new ArrayList<>());
        return getCurrentGameState(roomNo);
    }

    private GameState getCurrentGameState(long roomNo) {
        GameState gameState = new GameState();
        gameState.setMoves(roomMoves.getOrDefault(roomNo, new ArrayList<>()));
        gameState.setLosingPlayer(null);
        gameState.setCurrentTurn(roomPlayers.getOrDefault(roomNo, new ArrayList<>()).isEmpty() ? null : roomPlayers.get(roomNo).get(roomCurrentPlayerIndex.getOrDefault(roomNo, 0)));
        gameState.setPlayers(roomPlayers.getOrDefault(roomNo, new ArrayList<>()));
        return gameState;
    }
}
