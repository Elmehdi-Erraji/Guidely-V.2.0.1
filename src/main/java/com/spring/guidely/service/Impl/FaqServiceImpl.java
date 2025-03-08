package com.spring.guidely.service.Impl;

import com.spring.guidely.entities.Faq;
import com.spring.guidely.repository.FaqRepository;
import com.spring.guidely.service.FaqService;
import com.spring.guidely.web.error.FaqNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqRepository;

    public FaqServiceImpl(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public Page<Faq> getAllFaqs(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }

    @Override
    public Faq getFaqById(UUID id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new FaqNotFoundException("FAQ not found with id: " + id));
    }

    @Override
    public Faq createFaq(Faq faq) {
        return faqRepository.save(faq);
    }

    @Override
    public Faq updateFaq(UUID id, Faq faq) {
        Faq existingFaq = getFaqById(id);
        existingFaq.setQuestion(faq.getQuestion());
        existingFaq.setAnswer(faq.getAnswer());
        existingFaq.setCategory(faq.getCategory());
        // Optionally, update createdBy if needed (usually it's not changed)
        existingFaq.setCreatedBy(faq.getCreatedBy());
        return faqRepository.save(existingFaq);
    }

    @Override
    public void deleteFaq(UUID id) {
        Faq existingFaq = getFaqById(id);
        faqRepository.delete(existingFaq);
    }
}
