package com.spring.guidely.web.vm.ticker;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketResponseVM {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String createdByName;
    private String assignedToName;
    private LocalDateTime createdAt;
}
