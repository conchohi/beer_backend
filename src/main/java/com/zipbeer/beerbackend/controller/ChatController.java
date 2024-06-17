package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage/{roomNo}")
    @SendTo("/topic/{roomNo}")
    public ChatMessage sendMessage(@PathVariable String roomNo, ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{roomNo}")
    @SendTo("/topic/{roomNo}")
    public ChatMessage addUser(@PathVariable String roomNo, ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.leaveUser/{roomNo}")
    @SendTo("/topic/{roomNo}")
    public ChatMessage leaveUser(@PathVariable String roomNo, ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        return chatMessage;
    }
}
