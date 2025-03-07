package com.spring.guidely;

import com.spring.guidely.config.RabbitMQConfig;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Ticket;
import com.spring.guidely.entities.enums.TicketPriority;
import com.spring.guidely.entities.enums.TicketStatus;
import com.spring.guidely.repository.TicketRepository;
import com.spring.guidely.service.Impl.TicketServiceImpl;
import com.spring.guidely.service.TicketAssignmentService;
import com.spring.guidely.web.error.DuplicateTicketException;
import com.spring.guidely.web.error.NoSupportAgentAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketAssignmentService ticketAssignmentService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    private Ticket ticket;
    private AppUser createdBy;
    private AppUser assignedAgent;
    private AppUser newAgent;

    @BeforeEach
    public void setUp() {
        // Create sample AppUser objects
        createdBy = new AppUser();
        createdBy.setId(UUID.randomUUID());
        createdBy.setName("Creator User");
        createdBy.setEmail("creator@example.com");

        assignedAgent = new AppUser();
        assignedAgent.setId(UUID.randomUUID());
        assignedAgent.setName("Assigned Agent");
        assignedAgent.setEmail("agent@example.com");

        newAgent = new AppUser();
        newAgent.setId(UUID.randomUUID());
        newAgent.setName("New Agent");
        newAgent.setEmail("newagent@example.com");

        // Create a sample ticket
        ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.HIGH);
        ticket.setCreatedBy(createdBy);
        ticket.setAssignedTo(assignedAgent);
    }

    // ----------- Tests for saveTicket() -----------------

    @Test
    public void testSaveTicketSuccessWithNoAssignedAgent() {
        // New ticket with no assigned agent.
        Ticket newTicket = new Ticket();
        newTicket.setTitle("Unique Ticket");
        newTicket.setDescription("New Ticket Description");
        newTicket.setStatus(TicketStatus.OPEN);
        newTicket.setPriority(TicketPriority.MEDIUM);
        newTicket.setCreatedBy(createdBy);
        newTicket.setAssignedTo(null);

        when(ticketRepository.existsByTitle("Unique Ticket")).thenReturn(false);
        when(ticketAssignmentService.assignTicket(newTicket)).thenReturn(assignedAgent);
        when(ticketRepository.save(newTicket)).thenReturn(newTicket);
        doNothing().when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), Optional.ofNullable(any()));

        Ticket result = ticketServiceImpl.saveTicket(newTicket);

        assertNotNull(result);
        assertEquals("Unique Ticket", result.getTitle());
        assertNotNull(result.getAssignedTo());
        verify(ticketRepository, times(1)).existsByTitle("Unique Ticket");
        verify(ticketAssignmentService, times(1)).assignTicket(newTicket);
    }

    @Test
    public void testSaveTicketDuplicateTitleThrowsException() {
        Ticket duplicateTicket = new Ticket();
        duplicateTicket.setTitle("Duplicate Ticket");
        duplicateTicket.setDescription("Desc");
        duplicateTicket.setStatus(TicketStatus.OPEN);
        duplicateTicket.setPriority(TicketPriority.LOW);
        duplicateTicket.setCreatedBy(createdBy);
        duplicateTicket.setAssignedTo(null);

        // Simulate that a ticket with this title already exists.
        when(ticketRepository.existsByTitle("Duplicate Ticket")).thenReturn(true);

        DuplicateTicketException exception = assertThrows(DuplicateTicketException.class, () -> {
            ticketServiceImpl.saveTicket(duplicateTicket);
        });
        assertTrue(exception.getMessage().contains("Duplicate Ticket"));
    }

    @Test
    public void testSaveTicketNoSupportAgentAvailableThrowsException() {
        Ticket newTicket = new Ticket();
        newTicket.setTitle("No Agent Ticket");
        newTicket.setDescription("Desc");
        newTicket.setStatus(TicketStatus.OPEN);
        newTicket.setPriority(TicketPriority.HIGH);
        newTicket.setCreatedBy(createdBy);
        newTicket.setAssignedTo(null);

        when(ticketRepository.existsByTitle("No Agent Ticket")).thenReturn(false);
        when(ticketAssignmentService.assignTicket(newTicket)).thenReturn(null);

        NoSupportAgentAvailableException exception = assertThrows(NoSupportAgentAvailableException.class, () -> {
            ticketServiceImpl.saveTicket(newTicket);
        });
        assertTrue(exception.getMessage().contains("No support agent available"));
    }

    // ----------- Tests for updateTicket() -----------------


    // ----------- Tests for getAllTickets() -----------------

    @Test
    public void testGetAllTickets() {
        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketServiceImpl.getAllTickets();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ticket.getId(), result.get(0).getId());
    }

    // ----------- Tests for getTicketById() -----------------

    @Test
    public void testGetTicketByIdFound() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        Ticket result = ticketServiceImpl.getTicketById(ticket.getId());
        assertNotNull(result);
        assertEquals(ticket.getTitle(), result.getTitle());
    }

    @Test
    public void testGetTicketByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ticketServiceImpl.getTicketById(id);
        });
        assertTrue(exception.getMessage().contains("Ticket not found with id"));
    }

    // ----------- Tests for deleteTicket() -----------------

    @Test
    public void testDeleteTicket() {
        UUID id = ticket.getId();
        doNothing().when(ticketRepository).deleteById(id);

        ticketServiceImpl.deleteTicket(id);
        verify(ticketRepository, times(1)).deleteById(id);
    }

    // ----------- Tests for reassignTicket() -----------------

    @Test
    public void testReassignTicketSuccess() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketAssignmentService.assignTicket(ticket)).thenReturn(newAgent);
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketServiceImpl.reassignTicket(ticket.getId());
        assertNotNull(result);
        assertEquals(newAgent.getId(), result.getAssignedTo().getId());
    }

    @Test
    public void testReassignTicketNoSupportAgentThrowsException() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketAssignmentService.assignTicket(ticket)).thenReturn(null);

        NoSupportAgentAvailableException exception = assertThrows(NoSupportAgentAvailableException.class, () -> {
            ticketServiceImpl.reassignTicket(ticket.getId());
        });
        assertTrue(exception.getMessage().contains("No available support agent found"));
    }
}
