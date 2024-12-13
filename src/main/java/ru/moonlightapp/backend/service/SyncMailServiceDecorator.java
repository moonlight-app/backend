package ru.moonlightapp.backend.service;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
public final class SyncMailServiceDecorator extends MailService {

    public SyncMailServiceDecorator(JavaMailSender mailSender) {
        super(mailSender);
    }

    @Override
    public void sendMail(String receiverAddress, String subject, String plainText, String html) throws MessagingException, UnsupportedEncodingException {
        log.debug("Sending email to '{}'...", receiverAddress);
        super.sendMail(receiverAddress, subject, plainText, html);
        log.info("An email has been sent to '{}'.", receiverAddress);
    }

}
