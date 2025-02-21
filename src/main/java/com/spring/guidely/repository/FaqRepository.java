package com.spring.guidely.repository;

import com.spring.guidely.entities.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FaqRepository extends JpaRepository<Faq, UUID> {
}