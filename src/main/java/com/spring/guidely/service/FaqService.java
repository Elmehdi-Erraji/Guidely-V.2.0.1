package com.spring.guidely.service;

import com.spring.guidely.entities.Faq;
import com.spring.guidely.repository.FaqRepository;
import com.spring.guidely.web.error.FaqNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class FaqService {
    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public Faq getFaqById(UUID id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new FaqNotFoundException("FAQ not found with id: " + id));
    }

    public Faq createFaq(Faq faq) {
        return faqRepository.save(faq);
    }

    public Faq updateFaq(UUID id, Faq faq) {
        Faq existingFaq = getFaqById(id);
        existingFaq.setQuestion(faq.getQuestion());
        existingFaq.setAnswer(faq.getAnswer());
        existingFaq.setCategory(faq.getCategory());
        existingFaq.setCreatedBy(faq.getCreatedBy());
        return faqRepository.save(existingFaq);
    }

    public void deleteFaq(UUID id) {
        Faq existingFaq = getFaqById(id);
        faqRepository.delete(existingFaq);
    }
}
