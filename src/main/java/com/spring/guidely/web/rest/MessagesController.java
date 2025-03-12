package com.spring.guidely.web.rest;

import com.spring.guidely.entities.Message;
import com.spring.guidely.entities.enums.MessageType;
import com.spring.guidely.repository.MessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    private final MessageRepository messageRepository;

    public MessagesController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Endpoint to create a new message
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        // Set default type if not provided (e.g., CHAT)
        if(message.getType() == null) {
            message.setType(MessageType.CHAT);
        }
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    // Endpoint to get all messages for a specific ticket
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Message>> getMessagesByTicket(@PathVariable UUID ticketId) {
        List<Message> messages = messageRepository.findByTicketId(ticketId);
        return ResponseEntity.ok(messages);
    }

    // Additional endpoints can be added for filtering by sender, receiver, or type if needed
}
