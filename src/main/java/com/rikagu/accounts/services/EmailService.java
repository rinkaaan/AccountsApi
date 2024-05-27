package com.rikagu.accounts.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    private final SendGrid sendGrid;
    private final Environment environment;

    public EmailService(SendGrid sendGrid, Environment environment) {
        this.sendGrid = sendGrid;
        this.environment = environment;
    }

    public void sendEmail(String to, String subject, String body) {
        Email from = new Email(environment.getProperty("spring.sendgrid.from-email"));
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            int statusCode = response.getStatusCode();
            System.out.println(statusCode);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
