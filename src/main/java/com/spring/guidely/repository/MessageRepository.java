package com.spring.guidely.repository;


import com.spring.guidely.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByTicketId(UUID ticketId);
}
