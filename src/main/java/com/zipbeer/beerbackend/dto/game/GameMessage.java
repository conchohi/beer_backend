package com.zipbeer.beerbackend.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage {
    private String player;
    private List<Integer> numbers;
}
