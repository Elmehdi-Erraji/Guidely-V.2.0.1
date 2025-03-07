package com.spring.guidely.service.Impl;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Ticket;
import com.spring.guidely.entities.enums.TicketStatus;
import com.spring.guidely.repository.AppUserRepository;
import com.spring.guidely.repository.TicketRepository;
import com.spring.guidely.repository.UserRepository;
import com.spring.guidely.service.TicketAssignmentService;
import com.spring.guidely.web.error.NoSupportAgentAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TicketAssignmentServiceImpl implements TicketAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(TicketAssignmentServiceImpl.class);

    private final UserRepository appUserRepository;
    private final TicketRepository ticketRepository;

    public TicketAssignmentServiceImpl(UserRepository appUserRepository, TicketRepository ticketRepository) {
        this.appUserRepository = appUserRepository;
        this.ticketRepository = ticketRepository;
    }


    @Override
    public AppUser assignTicket(Ticket ticket) {
        List<AppUser> supportAgents = appUserRepository.findByRoleName("SUPPORT_AGENT");
        if (supportAgents.isEmpty()) {
            throw new NoSupportAgentAvailableException("No support agents available for assignment");
        }
        List<String> closedStatuses = Arrays.asList(
                TicketStatus.RESOLVED.name(),
                TicketStatus.CLOSED.name()
        );
        AppUser leastLoadedAgent = null;
        int minTicketCount = Integer.MAX_VALUE;
        for (AppUser agent : supportAgents) {
            int openTicketCount = ticketRepository.countByAssignedToAndStatusNotIn(
                    agent.getId(), closedStatuses.get(0), closedStatuses.get(1));
            if (openTicketCount < minTicketCount) {
                minTicketCount = openTicketCount;
                leastLoadedAgent = agent;
            }
        }
        if (leastLoadedAgent == null) {
            throw new NoSupportAgentAvailableException("No available support agent found");
        }
        logger.info("Assigning ticket {} to support agent {} with {} open tickets",
                ticket.getId(), leastLoadedAgent.getUsername(), minTicketCount);
        return leastLoadedAgent;
    }


}
