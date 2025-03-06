package com.spring.guidely.web.vm.faq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;


@Data
public class FaqCreateRequestVM {
    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    @NotNull(message = "CreatedBy id is required")
    private UUID createdById;

    @NotNull(message = "Category id is required")
    private UUID categoryId;
}
