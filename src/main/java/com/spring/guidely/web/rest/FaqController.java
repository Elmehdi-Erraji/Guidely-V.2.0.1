package com.spring.guidely.web.rest;

import com.spring.guidely.entities.Faq;
import com.spring.guidely.service.FaqService;
import com.spring.guidely.web.vm.faq.FaqCreateRequestVM;
import com.spring.guidely.web.vm.faq.FaqResponseVM;
import com.spring.guidely.web.vm.mapers.FaqVMMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    private final FaqService faqService;
    private final FaqVMMapper faqVMMapper;

    public FaqController(FaqService faqService, FaqVMMapper faqVMMapper) {
        this.faqService = faqService;
        this.faqVMMapper = faqVMMapper;
    }

    @GetMapping
    public ResponseEntity<Page<FaqResponseVM>> getAllFaqs(Pageable pageable) {
        Page<Faq> faqs = faqService.getAllFaqs(pageable);
        Page<FaqResponseVM> response = faqs.map(faqVMMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaqResponseVM> getFaqById(@PathVariable UUID id) {
        Faq faq = faqService.getFaqById(id);
        return ResponseEntity.ok(faqVMMapper.toResponse(faq));
    }

    @PostMapping
    public ResponseEntity<FaqResponseVM> createFaq(@Valid @RequestBody FaqCreateRequestVM faqCreateRequest) {
        Faq faq = faqVMMapper.toEntity(faqCreateRequest);
        Faq createdFaq = faqService.createFaq(faq);
        return ResponseEntity.ok(faqVMMapper.toResponse(createdFaq));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FaqResponseVM> updateFaq(@PathVariable UUID id,
                                                   @Valid @RequestBody FaqCreateRequestVM faqCreateRequest) {
        Faq faq = faqVMMapper.toEntity(faqCreateRequest);
        Faq updatedFaq = faqService.updateFaq(id, faq);
        return ResponseEntity.ok(faqVMMapper.toResponse(updatedFaq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable UUID id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}
