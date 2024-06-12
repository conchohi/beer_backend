package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.game.GameMessage;
import com.zipbeer.beerbackend.dto.game.GameState;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, GameState> gameRooms = new HashMap<>();

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/start/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = new GameState(gameMessage.getPlayers());
        gameRooms.put(roomNo, gameState);
        return gameState;
    }

    @MessageMapping("/move/{roomNo}")
    public void processMove(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            gameState.processMove(gameMessage);
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
    }

    @MessageMapping("/reset/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState resetGame(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            gameState.reset();
        }
        return gameState;
    }
}
