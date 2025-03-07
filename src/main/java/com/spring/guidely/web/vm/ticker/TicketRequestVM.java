package com.spring.guidely.web.vm.ticker;

import lombok.Data;

import java.util.UUID;

@Data
public class TicketRequestVM {
    private String title;
    private String description;
    private String status;
    private String priority; // e.g., "HIGH"
    private String createdBy; // Now a simple UUID string (e.g., "de167aa6-83b2-4ca8-99be-f60f1a66e5e9")
    private String assignedTo; // Optional; can be null or omitted.
}