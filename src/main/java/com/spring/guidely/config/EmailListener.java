package com.spring.guidely.config;

import com.spring.guidely.config.RabbitMQConfig;
import com.spring.guidely.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailListener {

    private final EmailService emailService;

    public EmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmail(Map<String, String> emailData) {
        String to = emailData.get("to");
        String subject = emailData.get("subject");
        String html = emailData.get("html");
        emailService.sendEmail(to, subject, html);
    }
}
