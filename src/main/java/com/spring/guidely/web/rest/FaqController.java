package com.spring.guidely.web.rest;

import com.spring.guidely.entities.Faq;
import com.spring.guidely.service.FaqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getAllFaqs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faq> getFaqById(@PathVariable UUID id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping
    public ResponseEntity<Faq> createFaq(@RequestBody Faq faq) {
        return ResponseEntity.ok(faqService.createFaq(faq));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faq> updateFaq(@PathVariable UUID id, @RequestBody Faq faq) {
        return ResponseEntity.ok(faqService.updateFaq(id, faq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable UUID id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
