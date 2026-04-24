package com.coursivo.coursivo_backend.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService {

    private static final Logger log = LoggerFactory.getLogger(SendGridEmailService.class);

    private final SendGrid sendGrid;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;

    public SendGridEmailService(@Value("${sendgrid.api-key}") String apiKey) {
        // If apiKey is completely empty or just the placeholder, this will throw an error or not work,
        // but it's safe to instantiate the client
        this.sendGrid = new SendGrid(apiKey);
    }

    public void sendEnrollmentConfirmationToStudent(String toEmail, String studentName, String courseTitle) {
        String subject = "You're enrolled in " + courseTitle + "!";
        String body = """
                <html><body>
                <h2>Welcome to %s, %s!</h2>
                <p>You have successfully enrolled in <strong>%s</strong>.</p>
                <p>Start learning now by visiting your dashboard.</p>
                <p>Happy learning,<br/>The Coursivo Team</p>
                </body></html>
                """.formatted(courseTitle, studentName, courseTitle);
        sendEmail(toEmail, subject, body);
    }

    public void sendNewEnrollmentAlertToInstructor(String toEmail, String instructorName, String studentName,
            String courseTitle) {
        String subject = "New student enrolled in " + courseTitle;
        String body = """
                <html><body>
                <h2>New Enrollment Alert</h2>
                <p>Hi %s,</p>
                <p><strong>%s</strong> just enrolled in your course <strong>%s</strong>.</p>
                <p>View your instructor dashboard for details.</p>
                <p>The Coursivo Team</p>
                </body></html>
                """.formatted(instructorName, studentName, courseTitle);
        sendEmail(toEmail, subject, body);
    }

    public void sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            log.info("Attempting to send email to {} via SendGrid...", toEmail);
            Response response = sendGrid.api(request);

            log.info("SendGrid response — Status: {}, Body: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("✅ Email sent successfully to {}", toEmail);
            } else {
                log.error("❌ Failed to send email. Status: {}, Body: {}",
                    response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid returned status " + response.getStatusCode());
            }
        } catch (IOException ex) {
            log.error("IO Exception while sending email: {}", ex.getMessage());
            throw new RuntimeException("Failed to send email via SendGrid", ex);
        }
    }
}
