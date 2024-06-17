package com.zipbeer.beerbackend.dto.game;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GameMessage {
    private String player;
    private List<Integer> numbers; // 게임에서 사용하는 숫자 목록
    private List<String> players; // 게임 시작 시 플레이어 목록
    private String guess; // 정답 맞추기 시 사용
    private String voteFor; // 투표 시 사용
    private String content; // 주제 전달 시 사용
    private String a;
    private List<String> completedPlayers;
    // 주제 전달을 위한 생성자 추가
    public GameMessage(String player, String content) {
        this.player = player;
        this.content = content;
    }
}
