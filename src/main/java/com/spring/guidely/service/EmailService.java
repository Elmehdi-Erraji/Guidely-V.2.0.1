package com.spring.guidely.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an HTML email.
     *
     * @param to      The recipient's email address.
     * @param subject The email subject.
     * @param html    The HTML content of the email.
     */
    public void sendEmail(String to, String subject, String html) {
        try {
            // Create a MimeMessage
            MimeMessage message = mailSender.createMimeMessage();

            // Create a MimeMessageHelper to set the email details
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set the recipient, subject, and HTML content
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true indicates HTML content

            // Send the email
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}