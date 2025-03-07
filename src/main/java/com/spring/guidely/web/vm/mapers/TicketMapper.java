package com.spring.guidely.web.vm.mapers;

import com.spring.guidely.entities.Ticket;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.enums.TicketPriority;
import com.spring.guidely.entities.enums.TicketStatus;
import com.spring.guidely.web.vm.ticker.TicketRequestVM;
import com.spring.guidely.web.vm.ticker.TicketResponseVM;

public class TicketMapper {

    // Converts a TicketRequestVM to a Ticket entity.
    // Notice that we pass in already loaded AppUser entities for createdBy and assignedTo.
    public static Ticket toEntity(TicketRequestVM vm, AppUser createdBy, AppUser assignedTo) {
        Ticket ticket = new Ticket();
        ticket.setTitle(vm.getTitle());
        ticket.setDescription(vm.getDescription());
        ticket.setStatus(Enum.valueOf(TicketStatus.class, vm.getStatus()));
        ticket.setPriority(Enum.valueOf(TicketPriority.class, vm.getPriority()));
        ticket.setCreatedBy(createdBy);
        ticket.setAssignedTo(assignedTo);
        return ticket;
    }

    // Converts a Ticket entity to a TicketResponseVM.
    public static TicketResponseVM toResponseVM(Ticket ticket) {
        TicketResponseVM vm = new TicketResponseVM();
        vm.setId(ticket.getId());
        vm.setTitle(ticket.getTitle());
        vm.setDescription(ticket.getDescription());
        vm.setStatus(ticket.getStatus().name());
        vm.setPriority(ticket.getPriority().name());
        vm.setCreatedByName(ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName() : null);
        vm.setAssignedToName(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName() : null);
        return vm;
    }
}
