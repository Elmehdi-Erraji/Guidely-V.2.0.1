package com.spring.guidely.service;

import com.spring.guidely.entities.Faq;
import java.util.List;
import java.util.UUID;

public interface FaqService {
    List<Faq> getAllFaqs();
    Faq getFaqById(UUID id);
    Faq createFaq(Faq faq);
    Faq updateFaq(UUID id, Faq faq);
    void deleteFaq(UUID id);
}
