package com.spring.guidely.service;

public interface EmailService {
    /**
     * Sends an HTML email.
     *
     * @param to      The recipient's email address.
     * @param subject The email subject.
     * @param html    The HTML content of the email.
     */
    void sendEmail(String to, String subject, String html);
}
