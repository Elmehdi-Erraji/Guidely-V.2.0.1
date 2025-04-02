package com.spring.guidely.service;

import com.spring.guidely.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket saveTicket(Ticket ticket);

    List<Ticket> getAllTickets();

    Ticket getTicketById(UUID id);

    void deleteTicket(UUID id);

    Ticket updateTicket(Ticket ticket);

    Ticket reassignTicket(UUID ticketId);

    Page<Ticket> getTicketsByCreatedBy(UUID userId, Pageable pageable);

    Page<Ticket> getTicketsAssignedTo(UUID userId, Pageable pageable);
}
