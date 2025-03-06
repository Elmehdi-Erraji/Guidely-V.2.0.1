package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Ticket;

public interface TicketAssignmentService {
    AppUser assignTicket(Ticket ticket);

}
