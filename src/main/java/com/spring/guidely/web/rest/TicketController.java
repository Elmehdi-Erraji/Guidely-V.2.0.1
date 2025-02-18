package com.spring.guidely.web.rest;

import com.spring.guidely.entities.Ticket;
import com.spring.guidely.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket savedTicket = ticketService.saveTicket(ticket);
        return ResponseEntity.ok(savedTicket);
    }


    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket( @PathVariable UUID id,@RequestBody Ticket ticketDetails) {
        Ticket existingTicket = ticketService.getTicketById(id);
        if (existingTicket == null) {
            return ResponseEntity.notFound().build();
        }
        existingTicket.setTitle(ticketDetails.getTitle());
        existingTicket.setDescription(ticketDetails.getDescription());
        existingTicket.setStatus(ticketDetails.getStatus());
        existingTicket.setPriority(ticketDetails.getPriority());
        existingTicket.setAssignedTo(ticketDetails.getAssignedTo());

        Ticket updatedTicket = ticketService.saveTicket(existingTicket);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        Ticket existingTicket = ticketService.getTicketById(id);
        if (existingTicket == null) {
            return ResponseEntity.notFound().build();
        }
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
