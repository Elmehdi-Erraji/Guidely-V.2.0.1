package com.spring.guidely.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status;
    // Could be an enum: e.g., OPEN, IN_PROGRESS, CLOSED

    @Column
    private String priority;
    // Could be an enum: e.g., LOW, MEDIUM, HIGH

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to")
    private AppUser assignedTo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Prevents infinite recursion in Lombok's toString
    @EqualsAndHashCode.Exclude // Prevents infinite recursion in equals & hashcode
    private List<Message> messages = new ArrayList<>();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setTicket(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setTicket(null);
    }
}
