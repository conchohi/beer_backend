package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.game.BalanceTopic;
import com.zipbeer.beerbackend.dto.game.GameMessage;
import com.zipbeer.beerbackend.dto.game.GameState;
import com.zipbeer.beerbackend.dto.game.LiarTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, GameState> gameRooms = new HashMap<>();
    private final Map<String, LocalDateTime> endTime = new HashMap<>();
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
        } else if(gameState.getCurrentGame().equals("catchMind")) {
            return gameState;
        } else {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        gameState.setCurrentGame("catchMind");
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
        } else if (gameState.getCurrentGame().equals("character")) {
            return gameState;
        } else {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        gameState.setCurrentGame("character");
        gameState.setTopic(generateTopic("character", roomNo));
        gameState.setTimeLeft(10); // 초기 타이머를 10초로 설정
        return gameState;
    }


    @MessageMapping("/startLiarGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState startLiarGame(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState == null) {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        } else if(gameState.getCurrentGame().equals("liar")) {
            long timeLeft = Duration.between(LocalDateTime.now(),endTime.get(roomNo)).toSeconds();
            gameState.setTimeLeft(timeLeft);
            LiarTopic topic = gameState.getLiarTopic();
            for (String player : gameState.getPlayers()) {
                //라이어는 주제만, 플레이어는 주제 + 단어
                String playerTopic  = player.equals(gameState.getLiar()) ? topic.getSubject() : topic.toString();
                messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/topic", new GameMessage(player, playerTopic));
            }
            return gameState;
        } else {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        }
        gameState.setCurrentGame("liar");
        //끝나는 시간 현재시간에 5분 뒤
        endTime.put(roomNo, LocalDateTime.now().plusSeconds(300L));
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
        long timeLeft = Duration.between(LocalDateTime.now(),endTime.get(roomNo)).toSeconds();
        gameState.setTimeLeft(timeLeft);
        return gameState;
    }

    @MessageMapping("/endLiarGame/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState endLiarGame(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        gameState.endGame();
        gameState.setMessage(gameState.getMostVoted());
        gameRooms.remove(roomNo);
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
        } else if(gameState.getCurrentGame().equals("bomb")) {
            long timeLeft = Duration.between(LocalDateTime.now(),endTime.get(roomNo)).toSeconds();
            gameState.setTimeLeft(timeLeft);
            return gameState;
        } else {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        }
        gameState.setCurrentGame("bomb");
        endTime.put(roomNo, LocalDateTime.now().plusSeconds(random.nextInt(15)+5));
        // 폭탄 시작 설정
        String bomb = gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size()));
        gameState.setBomb(bomb);

        long timeLeft = Duration.between( LocalDateTime.now(),endTime.get(roomNo)).toSeconds();
        gameState.setTimeLeft(timeLeft);
        return gameState;
    }
    @MessageMapping("/sendBomb/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState sendBomb(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        String bomb = gameMessage.getPlayer();
        long timeLeft = Duration.between(LocalDateTime.now(),endTime.get(roomNo)).toSeconds();
        gameState.setTimeLeft(timeLeft);
        //전송한 폭탄
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
        } else if(gameState.getCurrentGame().equals("shoutInSilence")) {
            return gameState;
        } else {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
            usedTopicsMap.remove(roomNo); // 게임 재시작 시 사용된 주제 초기화
        }
        gameState.setCurrentGame("shoutInSilence");
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
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", gameMessage.getPlayer());
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
        return gameState;
    }

    @MessageMapping("/charactergamehandleTimeExpired/{roomNo}")
    @SendTo("/topic/game/{roomNo}")
    public GameState handleTimeExpired(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            gameState.setTopic(generateTopic("character", roomNo));
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

    @MessageMapping("/erase/{roomNo}")
    public void eraseDrawing(@DestinationVariable String roomNo) {
        messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/erase", "clear");
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
        } else if (gameState.getCurrentGame().equals("chosung")) {
            return gameState;
        } else {
            gameState = new GameState(gameMessage.getPlayers());
            gameRooms.put(roomNo, gameState);
        }
        gameState.setCurrentGame("chosung");
        gameState.getGuessedWords().clear(); // guessedWords 초기화
        gameState.setCurrentTurn(gameMessage.getPlayers().get(random.nextInt(gameMessage.getPlayers().size())));
        gameState.setTopic(generateChosung());
        gameState.setTimeLeft(15); // 주제가 바뀔 때 타이머 초기화
        return gameState;
    }

    @MessageMapping("/guessChosung/{roomNo}")
    public void processGuessChosung(@DestinationVariable String roomNo, GameMessage gameMessage) {
        GameState gameState = gameRooms.get(roomNo);
        String guess = gameMessage.getGuess();
        String player = gameMessage.getPlayer();

        if (gameState != null) {
            if (gameState.getLastCorrectPlayers().contains(player)) {
                messagingTemplate.convertAndSendToUser(player, "/queue/errors", new GameMessage(player, "이미 통과하셨습니다."));
                return;
            }

            if (gameState.isWordGuessed(guess)) {
                messagingTemplate.convertAndSendToUser(player, "/queue/errors", new GameMessage(player, "이미 제출한 정답입니다."));
                return;
            }

            gameState.addGuessedWord(guess);
            gameState.getLastCorrectPlayers().add(player);

            if (gameState.getLastCorrectPlayers().size() == gameState.getPlayers().size() - 1) {
                List<String> playersNotGuessed = new ArrayList<>(gameState.getPlayers());
                playersNotGuessed.removeAll(gameState.getLastCorrectPlayers());

                if (!playersNotGuessed.isEmpty()) {
                    String lastPlayer = playersNotGuessed.get(0);
                    gameState.updateScore(lastPlayer, -1);
                    messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", player + "님이 '" + guess + "' 단어로 통과했습니다.");
                    messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/minusScore", lastPlayer + "님이 -1점 받았습니다.");
                    if (gameState.getScores().get(lastPlayer) <= -5) {
                        gameState.setLoser(lastPlayer);
                        gameState.endGame();
                        gameState.resetScores();
                        messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
                    } else {
                        gameState.setTopic(generateChosung());
                        gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size())));
                        gameState.getLastCorrectPlayers().clear();
                        gameState.getGuessedWords().clear();
                        gameState.setTimeLeft(15); // 주제가 바뀔 때 타이머 초기화
                        messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
                    }
                }
            } else {
                messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/correct", player + "님이 '" + guess + "' 단어로 통과했습니다.");
            }
            messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
        }
    }


    @MessageMapping("/timeoutChosung/{roomNo}")
    @SendTo("/topic/game/{roomNo}/timeout")
    public void handleTimeoutChosung(@DestinationVariable String roomNo) {
        GameState gameState = gameRooms.get(roomNo);
        if (gameState != null) {
            List<String> playersNotGuessed = new ArrayList<>(gameState.getPlayers());
            playersNotGuessed.removeAll(gameState.getLastCorrectPlayers());

            if (!playersNotGuessed.isEmpty()) {
                playersNotGuessed.forEach(player -> gameState.updateScore(player, -1));
                gameState.getLastCorrectPlayers().clear();

                messagingTemplate.convertAndSend("/topic/game/" + roomNo + "/timeout", playersNotGuessed);

                if (gameState.getPlayers().stream().anyMatch(player -> gameState.getScores().get(player) <= -5)) {
                    String losingPlayers = gameState.getPlayers().stream()
                            .filter(player -> gameState.getScores().get(player) <= -5)
                            .collect(Collectors.joining(", "));
                    gameState.setLoser(losingPlayers);
                    gameState.endGame();
                    gameState.resetScores();
                    messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
                } else {
                    gameState.setTopic(generateChosung());
                    gameState.setCurrentTurn(gameState.getPlayers().get(random.nextInt(gameState.getPlayers().size())));
                    gameState.setTimeLeft(15); // 주제가 바뀔 때 타이머 초기화
                    messagingTemplate.convertAndSend("/topic/game/" + roomNo, gameState);
                }
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
        String[] catchMindTopics = {
                "원숭이", "기린", "사과", "김", "배", "수박", "참외", "제비", "소방차", "캐리어", "비",
                "돼지", "사슴", "키보드", "사건", "경찰", "댄서", "고드름", "케이크", "마늘", "나비", "잠자리",
                "꽃", "태양", "달", "별", "자동차", "비행기", "기차", "연필", "책", "나무", "집", "우산",
                "모자", "토끼", "강아지", "고양이", "햄버거", "피자", "아이스크림", "물고기", "텔레비전",
                "시계", "컴퓨터", "전화기", "우체통", "신발", "양말", "안경", "컵", "빵", "자전거", "바나나",
                "바다", "산", "구름", "도넛", "새", "치즈", "호랑이", "침대", "의자", "책상",
                "버스", "트럭", "쥐", "코끼리", "지갑", "가방", "바지", "셔츠", "드레스", "악어"
        };

        String[] characterTopics = {
                "김세정", "설현", "수지", "아이유", "조이", "진세연", "채수빈", "카리나", "크리스탈", "혜리",
                "고마츠나나", "고윤정", "김태리", "다현", "로제", "류준열", "박서준", "사쿠라", "신민아", "아이린",
                "안유진", "윤아", "은하", "이진욱", "장원영", "전지현", "차은우", "카즈하", "한지민", "윈터",
                "김태희", "한가인", "이민정", "강동원", "고수", "공유", "송혜교", "박보검", "정해인", "김우빈",
                "송중기", "전도연", "김소현", "이종석", "이제훈", "손예진", "강하늘", "현빈", "마동석", "이민호",
                "박신혜", "김지원", "김수현", "주원", "박보영", "김유정", "남주혁", "도경수", "미연", "권은비",
                "김소연", "지수", "윤계상", "송지효", "류승룡", "조정석", "나연", "해원", "설윤", "말왕",
                "김혜수", "김고은", "정우성", "황정민", "유승호", "해린", "김채원", "침착맨", "김민주",
                "뷔","지드래곤", "비비", "제니", "나띠", "현아", "채영", "가을", "다니엘", "케이", "이정재",
                "이동욱", "손석구", "김남길", "지연", "리즈", "권나라", "장나라", "오연서", "나나", "한예슬",
                "서현진", "유인나", "신세경"
        };

        String[] shoutInSilenceTopics = {
                "원숭이", "코끼리", "펭귄", "고양이", "강아지", "토끼", "기린", "수영", "농구", "오토바이",
                "비행기", "가위", "겨울왕국", "마라톤", "매트릭스", "비", "돼지", "댄서", "낚시", "헐크",
                "라면", "타이타닉", "축구", "발레", "치타", "잠수", "연날리기", "볼링", "스키", "하이킹",
                "요가", "스파이더맨", "용", "슈퍼마켓", "카우보이", "해적", "우주비행사", "마법사", "카페",
                "피자", "로봇", "유령", "디스코", "서핑", "무지개", "달리기", "야구", "아이언맨", "자전거", "드럼", "테니스",
                "미용사", "농부", "요리사", "무술", "사자", "경찰", "우주",
                "꽃", "피아노", "산타", "골프", "박수"
        };


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
        gameState.setCurrentGame("baskin");
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
        gameState.setCurrentGame("balance");
        gameState.setBalanceTopic(balanceTopic);  // GameState에 BalanceTopic 설정
        gameState.setChoices(balanceTopic.getChoice0(), balanceTopic.getChoice1());
        gameState.setCurrentRound(1);  // 첫 번째 라운드 시작
        gameState.setTotalRounds(3);
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
        if (gameState != null) {// 완료된 플레이어 목록 초기화
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

    private BalanceTopic generateBalanceTopic(String roomNo) {
        BalanceTopic[] balanceTopics = {
                new BalanceTopic("카레맛 똥", "똥맛 카레"),
                new BalanceTopic("전공으로 완전 성공하기", "다양한 분야에서 활발히 활동하기"),
                new BalanceTopic("애인의 친구가 내 전애인", "내 전애인이 친구의 애인"),
                new BalanceTopic("시간을 멈추기", "투명해지기"),
                new BalanceTopic("100억 받고 해외에서만 살기", "60억상당의 부동산 받고 한국에서만 살기"),
                new BalanceTopic("과거를 보고 오기", "미래를 보고 오기"),
                new BalanceTopic("자가 치유", "순간 이동"),
                new BalanceTopic("빚이 30억인 이상형", "나만 보는 부자인 비호감"),
                new BalanceTopic("잠수 이별", "환승 이별"),
                new BalanceTopic("절친에게 10억 주기", "절친 잃고 100억 받기"),
                new BalanceTopic("여사친 남사친과 짝수 6박7일 여행하는 애인", "전 애인과 첫차까지 술 마신 애인"),
                new BalanceTopic("무한한 돈", "무한한 시간"),
                new BalanceTopic("맞춤법 지적하는 애인", "맞춤법 틀리는 애인"),
                new BalanceTopic("부랄친구와 키스하기", "친구 부랄에 키스하기"),
                new BalanceTopic("싫어하는 친구가 로또 당첨되고 1억받기", "그냥 살기"),
                new BalanceTopic("대머리 애인", "털복숭이 애인"),
                new BalanceTopic("팔만대장경 다 읽기", "대장내시경 팔만번 하기")
        };

        return balanceTopics[random.nextInt(balanceTopics.length)];
    }
}
