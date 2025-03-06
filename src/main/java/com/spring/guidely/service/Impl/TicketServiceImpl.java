package com.spring.guidely.service.Impl;

import com.spring.guidely.config.RabbitMQConfig;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Ticket;
import com.spring.guidely.repository.TicketRepository;
import com.spring.guidely.service.TicketAssignmentService;
import com.spring.guidely.service.TicketService;
import com.spring.guidely.web.error.DuplicateTicketException;
import com.spring.guidely.web.error.NoSupportAgentAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketRepository ticketRepository;
    private final TicketAssignmentService ticketAssignmentService;
    private final RabbitTemplate rabbitTemplate;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             TicketAssignmentService ticketAssignmentService,
                             RabbitTemplate rabbitTemplate) {
        this.ticketRepository = ticketRepository;
        this.ticketAssignmentService = ticketAssignmentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Ticket saveTicket(Ticket ticket) {

        if (ticket.getTitle() != null) {
            if (ticket.getId() == null) {
                if (ticketRepository.existsByTitle(ticket.getTitle())) {
                    throw new DuplicateTicketException("Ticket with title " + ticket.getTitle() + " already exists");
                }
            } else {
                ticketRepository.findByTitle(ticket.getTitle()).ifPresent(existing -> {
                    if (!existing.getId().equals(ticket.getId())) {
                        throw new DuplicateTicketException("Ticket with title " + ticket.getTitle() + " already exists");
                    }
                });
            }
        }

        if (ticket.getAssignedTo() == null) {
            AppUser assignedAgent = ticketAssignmentService.assignTicket(ticket);
            if (assignedAgent == null) {
                throw new NoSupportAgentAvailableException("No support agent available for assignment");
            }
            ticket.setAssignedTo(assignedAgent);
            sendTicketAssignmentEmail(ticket, assignedAgent);
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        Ticket existingTicket = getTicketById(ticket.getId());
        existingTicket.setTitle(ticket.getTitle());
        existingTicket.setDescription(ticket.getDescription());
        existingTicket.setStatus(ticket.getStatus());
        existingTicket.setPriority(ticket.getPriority());
        if (existingTicket.getAssignedTo() == null) {
            AppUser assignedAgent = ticketAssignmentService.assignTicket(existingTicket);
            if (assignedAgent == null) {
                throw new NoSupportAgentAvailableException("No support agent available for assignment");
            }
            existingTicket.setAssignedTo(assignedAgent);
        }
        return ticketRepository.save(existingTicket);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    @Override
    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public Ticket reassignTicket(UUID ticketId) {
        Ticket ticket = getTicketById(ticketId);
        AppUser newAgent = ticketAssignmentService.assignTicket(ticket);
        if (newAgent == null) {
            throw new NoSupportAgentAvailableException("No available support agent found for reassignment");
        }
        ticket.setAssignedTo(newAgent);
        return ticketRepository.save(ticket);
    }

    private void sendTicketAssignmentEmail(Ticket ticket, AppUser supportAgent) {
        String template;
        try (InputStream is = new ClassPathResource("templates/ticket-assignment.html").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            template = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ticket assignment email template", e);
        }

        String content = template
                .replace("{{agentName}}", supportAgent.getName())
                .replace("{{ticketTitle}}", ticket.getTitle());

        String subject = "New Ticket Assignment: " + ticket.getTitle();

        Map<String, String> emailData = new HashMap<>();
        emailData.put("to", supportAgent.getEmail());
        emailData.put("subject", subject);
        emailData.put("html", content);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, emailData);
        logger.info("Sent assignment email to {}", supportAgent.getEmail());
    }
}
