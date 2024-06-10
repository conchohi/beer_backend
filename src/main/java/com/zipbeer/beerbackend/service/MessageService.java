package com.zipbeer.beerbackend.service;


import com.zipbeer.beerbackend.entity.Message;
import com.zipbeer.beerbackend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public List<Message> getPublicMessages() {
        return messageRepository.findByRoomId("public");
    }

    public List<Message> getPrivateMessages(String username) {
        return messageRepository.findByReceiverName(username);
    }

    public List<Message> getMessages(String roomId) {
        return messageRepository.findByRoomId(roomId);
    }
}
