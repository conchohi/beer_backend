package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.ChatMessage;
import com.zipbeer.beerbackend.entity.Message;
import com.zipbeer.beerbackend.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public") // 경로 수정
    public Message sendMessage(@Payload Message message) {
        logger.info("Public message received: {}", message);
        messageService.saveMessage(message);
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message){
        String receiverName = message.getReceiverName();
        if (receiverName == null || receiverName.isEmpty()) {
            throw new IllegalArgumentException("Receiver name must not be null or empty");
        }
        simpMessagingTemplate.convertAndSendToUser(receiverName, "/private", message);
        System.out.println(message.toString());
        return message;
    }

    @GetMapping("/messages/private/{username}")
    @ResponseBody
    public List<Message> getPrivateMessages(@PathVariable("username") String username) {
        logger.info("Fetching private messages for user: {}", username);
        List<Message> messages = messageService.getPrivateMessages(username);
        logger.info("Fetched private messages: {}", messages);
        return messages;
    }

    @GetMapping("/chatroom/public/{roomId}")
    @ResponseBody
    public List<Message> getMessages(@PathVariable("roomId") String roomId) {
        logger.info("Fetching messages for room: {}", roomId);
        List<Message> messages = messageService.getMessages(roomId);
        logger.info("Fetched messages for room {}: {}", roomId, messages);
        return messages;
    }


}
