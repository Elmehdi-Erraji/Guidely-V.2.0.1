package com.spring.guidely.web.rest;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Ticket;
import com.spring.guidely.entities.enums.TicketPriority;
import com.spring.guidely.entities.enums.TicketStatus;
import com.spring.guidely.service.TicketService;
import com.spring.guidely.service.UserService;
import com.spring.guidely.web.error.DuplicateTicketException;
import com.spring.guidely.web.vm.mapers.TicketMapper;
import com.spring.guidely.web.vm.ticker.TicketRequestVM;
import com.spring.guidely.web.vm.ticker.TicketResponseVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService appUserService;

    public TicketController(TicketService ticketService, UserService appUserService) {
        this.ticketService = ticketService;
        this.appUserService = appUserService;
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody TicketRequestVM requestVM) {
        try {
            Ticket ticket = new Ticket();
            ticket.setTitle(requestVM.getTitle());
            ticket.setDescription(requestVM.getDescription());
            ticket.setStatus(TicketStatus.valueOf(requestVM.getStatus()));
            ticket.setPriority(TicketPriority.valueOf(requestVM.getPriority()));

            // Retrieve the createdBy user using its UUID string.
            AppUser createdBy = appUserService.getUserById(UUID.fromString(requestVM.getCreatedBy()))
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + requestVM.getCreatedBy()));
            ticket.setCreatedBy(createdBy);

            // Retrieve the assignedTo user if provided.
            if (requestVM.getAssignedTo() != null && !requestVM.getAssignedTo().isEmpty()) {
                AppUser assignedTo = appUserService.getUserById(UUID.fromString(requestVM.getAssignedTo()))
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + requestVM.getAssignedTo()));
                ticket.setAssignedTo(assignedTo);
            }

            Ticket savedTicket = ticketService.saveTicket(ticket);
            TicketResponseVM responseVM = TicketMapper.toResponseVM(savedTicket);
            return ResponseEntity.ok(responseVM);
        } catch (DuplicateTicketException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseVM>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        List<TicketResponseVM> response = tickets.stream()
                .map(TicketMapper::toResponseVM)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseVM> getTicketById(@PathVariable UUID id) {
        Ticket ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(TicketMapper.toResponseVM(ticket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable UUID id, @RequestBody TicketRequestVM requestVM) {
        try {
            Ticket existingTicket = ticketService.getTicketById(id);
            existingTicket.setTitle(requestVM.getTitle());
            existingTicket.setDescription(requestVM.getDescription());
            existingTicket.setStatus(TicketStatus.valueOf(requestVM.getStatus()));
            existingTicket.setPriority(TicketPriority.valueOf(requestVM.getPriority()));

            if (requestVM.getAssignedTo() != null && !requestVM.getAssignedTo().isEmpty()) {
                AppUser assignedTo = appUserService.getUserById(UUID.fromString(requestVM.getAssignedTo()))
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + requestVM.getAssignedTo()));
                existingTicket.setAssignedTo(assignedTo);
            }
            Ticket updatedTicket = ticketService.updateTicket(existingTicket);
            return ResponseEntity.ok(TicketMapper.toResponseVM(updatedTicket));
        } catch (DuplicateTicketException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
