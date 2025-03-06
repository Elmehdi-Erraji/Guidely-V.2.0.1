package com.spring.guidely.repository;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Ticket;
import com.spring.guidely.entities.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    boolean existsByTitle(String title);
    Optional<Ticket> findByTitle(String title);
    @Query(value = "SELECT count(*) FROM tickets " +
            "WHERE assigned_to = :agentId " +
            "AND status::text NOT IN (:status1, :status2)",
            nativeQuery = true)
    int countByAssignedToAndStatusNotIn(@Param("agentId") UUID agentId,
                                        @Param("status1") String status1,
                                        @Param("status2") String status2);

}