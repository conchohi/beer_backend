package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.game.BalanceTopic;
import com.zipbeer.beerbackend.dto.game.GameMessage;
import com.zipbeer.beerbackend.dto.game.GameState;
import com.zipbeer.beerbackend.dto.game.LiarTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, GameState> gameRooms = new HashMap<>();
    private final Map<String, Set<String>> usedTopicsMap = new HashMap<>();
    private final Random random = new Random();

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/clearCanvas/{roomNo}")
    @SendTo("/topic/game/{roomNo}/clearCanvas")
    public void clearCanvas(@DestinationVariable String roomNo) {
        // 클리어 메시지를 브로드캐스트합니다.
        messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/clearCanvas", new GameMessage("clear", ""));
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
        LiarTopic topic = generateLiarTopic(roomNo);
        gameState.setLiarTopic(topic);

        // 각 플레이어에게 주제 전달
        for (String player : gameState.getPlayers()) {
            //라이어는 주제만, 플레이어는 주제 + 단어
            String playerTopic  = player.equals(liar) ? topic.getSubject() : topic.toString();
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/topic", new GameMessage(player, playerTopic));
        }

        return gameState;
    }

    @MessageMapping("/endLiarGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState endLiarGame(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        gameState.endGame();
        gameState.setMessage(gameState.getMostVoted());
        return gameState;
    }
    @MessageMapping("/voteLiarGame/{roomNo}")
    public void voteLiarGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        gameState.addVote(gameMessage.getVoteFor());
    }

    @MessageMapping("/startBombGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startBombGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else {
            gameState.reset();
        }
        // 폭탄 시작 설정
        String bomb = gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()));
        gameState.setBomb(bomb);
        gameState.setTimeLeft(random.nextInt(60) + 120);
        return gameState;
    }
    @MessageMapping("/sendBomb/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState sendBomb(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        String bomb = gameMessage.getPlayer();
        // 폭탄 시작 설정
        gameState.setBomb(bomb);

        return gameState;
    }
    @MessageMapping("/startShoutInSilence/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startShoutInSilence(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else {
            gameState.reset();
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        gameState.setCurrentTurn(gameMessage.getPlayers().get(random.nextInt(gameMessage.getPlayers().size())));
        gameState.setTopic(generateTopic("shoutInSilence", roomNo));
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
            gameState.setTimeLeft(180); // 타이머 초기화
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
    }


    @MessageMapping("/guessShoutInSilence/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState processGuessShoutInSilence(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null && gameState.getTopic().equalsIgnoreCase(gameMessage.getGuess())) {
            gameState.updateScore(gameMessage.getPlayer()); // 정답 맞춘 사람 점수 증가
            if (gameState.getScores().get(gameMessage.getPlayer()) >= 5) { // 5점 도달 여부 확인
                gameState.setWinner(gameMessage.getPlayer());
                gameState.endGame();
                gameState.resetScores(); // 점수 초기화
            } else {
                gameState.setTopic(generateTopic("shoutInSilence", roomNo));
                gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()))); // 새로운 출제자 랜덤 선택
                gameState.setTimeLeft(180); // 정답 맞추면 타이머 초기화
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", gameMessage.getPlayer());
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
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

    @MessageMapping("/startChosungGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startChosungGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else {
            gameState.reset();
            usedTopicsMap.remove(roomNo);
        }
        gameState.getGuessedWords().clear(); // guessedWords 초기화
        gameState.setCurrentTurn(gameMessage.getPlayers().get(random.nextInt(gameMessage.getPlayers().size())));
        gameState.setTopic(generateChosung());
        return gameState;
    }

    @MessageMapping("/guessChosung/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState processGuessChosung(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        String guess = gameMessage.getGuess();
        if (gameState != null && !gameState.isWordGuessed(guess)) {
            gameState.addGuessedWord(guess);
            gameState.getLastCorrectPlayers().add(gameMessage.getPlayer());

            if (gameState.getLastCorrectPlayers().size() == gameState.getPlayers().size()) {
                String lastPlayer = gameState.getLastCorrectPlayers().get(gameState.getLastCorrectPlayers().size() - 1);
                gameState.updateScore(lastPlayer, -1);

                if (gameState.getScores().get(lastPlayer) <= -5) {
                    gameState.setLoser(lastPlayer);
                    gameState.endGame();
                    gameState.resetScores(); // 점수 초기화 추가
                    gameState.setLoser(null); // 패배자 초기화
                    messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
                } else {
                    gameState.setTopic(generateChosung());
                    gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size())));
                    gameState.getLastCorrectPlayers().clear();
                    gameState.getGuessedWords().clear(); // guessedWords 초기화
                }
                messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/minusScore", lastPlayer);
            }

            // Correct message with the guessed word
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", gameMessage.getPlayer() + "님이 '" + guess + "' 단어로 통과했습니다.");
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
        return gameState;
    }

    @MessageMapping("/timeoutChosung/{roomNo}")
    @SendTo("/topic/game/{roomNo}/timeout")
    public void handleTimeoutChosung(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            List<String> playersNotGuessed = new ArrayList<>(gameState.getPlayers());
            playersNotGuessed.removeAll(gameState.getLastCorrectPlayers());
            playersNotGuessed.forEach(player -> gameState.updateScore(player, -1));
            gameState.getLastCorrectPlayers().clear();

            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/timeout", playersNotGuessed);
            if (gameState.getPlayers().stream().anyMatch(player -> gameState.getScores().get(player) <= -5)) {
                String losingPlayers = gameState.getPlayers().stream()
                        .filter(player -> gameState.getScores().get(player) <= -5)
                        .collect(Collectors.joining(", "));
                gameState.setLoser(losingPlayers);
                gameState.endGame();
                gameState.resetScores(); // 점수 초기화j
                messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
            } else {
                gameState.setTopic(generateChosung());
                gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size())));
                messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
            }
        }
    }

    private String generateChosung() {
        String[] chosung = {"ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};
        return chosung[random.nextInt(chosung.length)] + chosung[random.nextInt(chosung.length)];
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


    private LiarTopic generateLiarTopic(String roomNo){
        LiarTopic[] liarGameTopics = {new LiarTopic("장소","바다"),new LiarTopic("장소","도서관"),new LiarTopic("장소","영화관"), new LiarTopic("장소","놀이공원"), new LiarTopic("장소","카페"), new LiarTopic("장소","학교"),
                new LiarTopic("동물","강아지"), new LiarTopic("동물","고양이"), new LiarTopic("동물","코끼리"), new LiarTopic("동물","원숭이"), new LiarTopic("동물","나무늘보"), new LiarTopic("동물","판다"), new LiarTopic("동물","스컹크"), new LiarTopic("동물","앵무새"),
                new LiarTopic("음식","치킨"), new LiarTopic("음식","피자"),
                new LiarTopic("음식","햄버거"), new LiarTopic("음식","제육볶음"), new LiarTopic("음식","돈까스"), new LiarTopic("음식","파스타"), new LiarTopic("음식","마라탕"), new LiarTopic("음식","탕후루")
        };

        return liarGameTopics[random.nextInt(liarGameTopics.length)];
    }
    private String generateTopic(String gameType, String roomNo) {
        String[] catchMindTopics = {"원숭이", "기린", "사과", "김", "배", "수박", "참외", "제비", "소방차", "캐리어", "비","돼지","사슴","키보드","사건","경찰","댄서","고드름","케이크","마늘","나비","잠자리"};
        String[] characterTopics = {"김세정", "설현", "수지", "아이유", "윤소희", "조이", "진세연", "채수빈", "카리나", "크리스탈", "혜리","고마츠나나","고윤정","김태리","다현","로제","류준열","박서준","사쿠라","신민아","아이린","안유진","윤아","은하","이진욱","장원영","전지현","차은우","카즈하","한지민"};
        String[] shoutInSilenceTopics = {"원숭이", "코끼리", "펭귄", "고양이", "강아지", "토끼", "기린", "수영", "농구", "오토바이", "비행기", "가위", "겨울왕국", "마라톤", "매트릭스", "비", "돼지","댄서","낚시","헐크","라면","타이타닉"};
//        String[] shoutInSilenceTopics = {"해변", "도서관", "영화관", "공원", "놀이공원", "카페", "서점", "박물관", "식당", "학교"};

        String[] topics;

        switch (gameType) {
            case "character":
                topics = characterTopics;
                break;
            case "shoutInSilence":
                topics = shoutInSilenceTopics;
                break;
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
    //베스킨라빈스
    @MessageMapping("/startBaskinRobbins31/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startBaskinRobbins31Game(@DestinationVariable String roomNo, GameMessage gameMessage) {
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
    @MessageMapping("/startBalanceGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startBalanceGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = new GameState(gameMessage.getPlayers());
        BalanceTopic balanceTopic = generateBalanceTopic(roomNo);  // 생성된 BalanceTopic 객체
        gameState.setBalanceTopic(balanceTopic);  // GameState에 BalanceTopic 설정
        gameState.setChoices(balanceTopic.getChoice0(), balanceTopic.getChoice1());
        gameState.setCurrentRound(1);  // 첫 번째 라운드 시작
        gameRooms.put(roomNo, gameState);
        messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        System.out.println(">>> START BALANCE GAME: " + gameState);  // 로그 추가
        return gameState;
    }

    @MessageMapping("/voteBalanceGame/{roomNo}")
    public void voteBalanceGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if ("choice0".equals(gameMessage.getVoteFor())) {
            gameState.setChoice0(gameState.getChoice0() + 1);
        } else if ("choice1".equals(gameMessage.getVoteFor())) {
            gameState.setChoice1(gameState.getChoice1() + 1);
        }
        gameState.addBalanceGameVote(gameMessage.getPlayer());  // 투표한 플레이어 추가

        messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        System.out.println(">>> VOTE BALANCE GAME: " + gameState);  // 로그 추가
    }

    @MessageMapping("/endRoundBalanceGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState endRoundBalanceGame(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            gameState.setCompletedPlayers(new ArrayList<>());  // 완료된 플레이어 목록 초기화
            if (gameState.getCurrentRound() >= gameState.getTotalRounds()) {
                gameState.endGame();
            } else {
                gameState.setCurrentRound(gameState.getCurrentRound() + 1);
                gameState.setChoice0(0);
                gameState.setChoice1(0);
                gameState.getBalanceGameVotes().clear();  // 투표 초기화
                BalanceTopic balanceTopic = generateBalanceTopic(roomNo);  // 새로운 BalanceTopic 객체 생성
                gameState.setChoices(balanceTopic.getChoice0(), balanceTopic.getChoice1());
                gameState.setBalanceTopic(balanceTopic);
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
            System.out.println(">>> END ROUND BALANCE GAME: " + gameState);  // 로그 추가
        }
        return gameState;
    }

    @MessageMapping("/updateGameState/{roomNo}")
    public void updateGameState(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        gameState.setCompletedPlayers(gameMessage.getCompletedPlayers());

        if (gameState.getCompletedPlayers().size() == gameState.getPlayers().size()) {
            endRoundBalanceGame(roomNo);
        } else {
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
        System.out.println(">>> UPDATE GAME STATE: " + gameState);  // 로그 추가
    }
    private BalanceTopic generateBalanceTopic(String roomNo) {
        BalanceTopic[] balanceTopics = {
                new BalanceTopic("카레맛 똥", "똥맛 카레"),
                new BalanceTopic("전공으로 완전 성공하기", "다양한 분야에서 활발히 활동하기"),
                new BalanceTopic("애인의 친구가 내 전애인", "내 전애인이 친구의 애인"),
                new BalanceTopic("시간을 멈추기", "투명해지기"),
                new BalanceTopic("100억 받고 해외에서만 살기", "60억상당의 부동산 받고 한국에서만 살기"),
                new BalanceTopic("과거를 보고 오기", "미래를 보고 오기"),
                new BalanceTopic("자가 치유", "순간 이동"),
                new BalanceTopic("무한한 돈", "무한한 시간")
        };

        return balanceTopics[random.nextInt(balanceTopics.length)];
    }
}