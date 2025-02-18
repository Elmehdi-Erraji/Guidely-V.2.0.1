package com.spring.guidely.service;

import com.spring.guidely.entities.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    // Create or Update
    Ticket saveTicket(Ticket ticket);

    // Read all
    List<Ticket> getAllTickets();

    // Read one by ID
    Ticket getTicketById(UUID id);

    // Delete
    void deleteTicket(UUID id);
}
