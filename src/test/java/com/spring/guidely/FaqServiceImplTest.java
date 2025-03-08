package com.spring.guidely;

import com.spring.guidely.entities.Faq;
import com.spring.guidely.repository.FaqRepository;
import com.spring.guidely.service.FaqService;
import com.spring.guidely.service.Impl.FaqServiceImpl;
import com.spring.guidely.web.error.FaqNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FaqServiceImplTest {

    @Mock
    private FaqRepository faqRepository;

    private FaqService faqService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        faqService = new FaqServiceImpl(faqRepository);
    }

    // Test getAllFaqs(Pageable pageable)
    @Test
    void getAllFaqs_Pageable_ReturnsPage() {
        // Prepare a page with one FAQ
        Faq faq = new Faq();
        faq.setId(UUID.randomUUID());
        faq.setQuestion("What is X?");
        faq.setAnswer("X is Y.");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Faq> faqPage = new PageImpl<>(Collections.singletonList(faq), pageable, 1);
        when(faqRepository.findAll(pageable)).thenReturn(faqPage);

        Page<Faq> result = faqService.getAllFaqs(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(faqRepository, times(1)).findAll(pageable);
    }

    // Test getFaqById() for existing FAQ
    @Test
    void getFaqById_ExistingId_ReturnsFaq() {
        UUID id = UUID.randomUUID();
        Faq faq = new Faq();
        faq.setId(id);
        faq.setQuestion("Q1");
        faq.setAnswer("A1");

        when(faqRepository.findById(id)).thenReturn(Optional.of(faq));

        Faq result = faqService.getFaqById(id);
        assertNotNull(result);
        assertEquals("Q1", result.getQuestion());
        verify(faqRepository, times(1)).findById(id);
    }

    // Test getFaqById() when FAQ is not found
    @Test
    void getFaqById_NonExistingId_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(faqRepository.findById(id)).thenReturn(Optional.empty());

        FaqNotFoundException exception = assertThrows(FaqNotFoundException.class, () -> {
            faqService.getFaqById(id);
        });
        assertEquals("FAQ not found with id: " + id, exception.getMessage());
        verify(faqRepository, times(1)).findById(id);
    }

    // Test createFaq()
    @Test
    void createFaq_Success() {
        Faq faq = new Faq();
        faq.setQuestion("What is Java?");
        faq.setAnswer("A programming language.");

        when(faqRepository.save(faq)).thenReturn(faq);

        Faq result = faqService.createFaq(faq);
        assertNotNull(result);
        assertEquals("What is Java?", result.getQuestion());
        verify(faqRepository, times(1)).save(faq);
    }

    // Test updateFaq() for success
    @Test
    void updateFaq_Success() {
        UUID id = UUID.randomUUID();
        Faq existingFaq = new Faq();
        existingFaq.setId(id);
        existingFaq.setQuestion("Old Q");
        existingFaq.setAnswer("Old A");

        Faq updateData = new Faq();
        updateData.setQuestion("New Q");
        updateData.setAnswer("New A");
        // Assume category and createdBy are updated as well if needed.
        updateData.setCategory(existingFaq.getCategory());
        updateData.setCreatedBy(existingFaq.getCreatedBy());

        when(faqRepository.findById(id)).thenReturn(Optional.of(existingFaq));
        when(faqRepository.save(existingFaq)).thenReturn(existingFaq);

        Faq result = faqService.updateFaq(id, updateData);
        assertNotNull(result);
        assertEquals("New Q", result.getQuestion());
        assertEquals("New A", result.getAnswer());
        verify(faqRepository, times(1)).findById(id);
        verify(faqRepository, times(1)).save(existingFaq);
    }

    // Test updateFaq() when FAQ is not found
    @Test
    void updateFaq_NonExistingId_ThrowsException() {
        UUID id = UUID.randomUUID();
        Faq updateData = new Faq();
        updateData.setQuestion("New Q");
        updateData.setAnswer("New A");

        when(faqRepository.findById(id)).thenReturn(Optional.empty());
        FaqNotFoundException exception = assertThrows(FaqNotFoundException.class,
                () -> faqService.updateFaq(id, updateData));
        assertEquals("FAQ not found with id: " + id, exception.getMessage());
        verify(faqRepository, times(1)).findById(id);
        verify(faqRepository, never()).save(any());
    }

    // Test deleteFaq() for success
    @Test
    void deleteFaq_Success() {
        UUID id = UUID.randomUUID();
        Faq faq = new Faq();
        faq.setId(id);
        faq.setQuestion("Test Q");

        when(faqRepository.findById(id)).thenReturn(Optional.of(faq));
        doNothing().when(faqRepository).delete(faq);

        assertDoesNotThrow(() -> faqService.deleteFaq(id));
        verify(faqRepository, times(1)).findById(id);
        verify(faqRepository, times(1)).delete(faq);
    }

    // Test deleteFaq() when FAQ is not found
    @Test
    void deleteFaq_NonExistingId_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(faqRepository.findById(id)).thenReturn(Optional.empty());

        FaqNotFoundException exception = assertThrows(FaqNotFoundException.class,
                () -> faqService.deleteFaq(id));
        assertEquals("FAQ not found with id: " + id, exception.getMessage());
        verify(faqRepository, times(1)).findById(id);
        verify(faqRepository, never()).delete(any());
    }
}
