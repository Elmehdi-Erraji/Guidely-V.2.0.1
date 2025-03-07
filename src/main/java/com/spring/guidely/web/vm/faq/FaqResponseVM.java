package com.spring.guidely.web.vm.faq;

import lombok.Data;

import java.util.UUID;

@Data
public class FaqResponseVM {
    private UUID id;
    private String question;
    private String answer;
    private UUID createdById;
    private UUID categoryId;
}
