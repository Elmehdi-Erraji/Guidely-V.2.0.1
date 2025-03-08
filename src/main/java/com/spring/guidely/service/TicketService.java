package com.spring.guidely.service;

import com.spring.guidely.entities.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket saveTicket(Ticket ticket);

    List<Ticket> getAllTickets();

    Ticket getTicketById(UUID id);

    void deleteTicket(UUID id);

    Ticket updateTicket(Ticket ticket);

    Ticket reassignTicket(UUID ticketId);
}
