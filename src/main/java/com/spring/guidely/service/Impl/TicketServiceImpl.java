package com.spring.guidely.service.Impl;

import com.spring.guidely.entities.Ticket;
import com.spring.guidely.repository.TicketRepository;
import com.spring.guidely.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {


    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketById(UUID id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        return optionalTicket.orElse(null);
        // or throw a custom NotFoundException if you prefer
    }

    @Override
    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }
}
