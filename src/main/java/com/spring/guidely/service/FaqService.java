package com.spring.guidely.service;

import com.spring.guidely.entities.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FaqService {
    Page<Faq> getAllFaqs(Pageable pageable);
    Faq getFaqById(UUID id);
    Faq createFaq(Faq faq);
    Faq updateFaq(UUID id, Faq faq);
    void deleteFaq(UUID id);
}
