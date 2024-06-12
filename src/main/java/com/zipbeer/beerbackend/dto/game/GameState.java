package com.zipbeer.beerbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameState {
    private List<String> moves;
    private String currentTurn;
    private String losingPlayer;
    private List<String> players;
}
