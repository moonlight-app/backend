package ru.moonlightapp.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public final class MailService {

    private static final ExecutorService MAIL_SENDING_TASKS_POOL = Executors.newVirtualThreadPerTaskExecutor();

    private final JavaMailSender mailSender;

    @Value("${server.mail.name}")
    private String senderName;
    @Value("${server.mail.address}")
    private String senderAddress;

    public void sendMailAsync(String receiverAddress, String subject, String plainText, String html) {
        MAIL_SENDING_TASKS_POOL.submit(() -> {
            try {
                log.debug("Sending email to '{}'...", receiverAddress);
                sendMail(receiverAddress, subject, plainText, html);
                log.info("An email has been sent to '{}'.", receiverAddress);
            } catch (Throwable ex) {
                log.error("Unable to an email!", ex);
            }
        });
    }

    public void sendMail(String receiverAddress, String subject, String plainText, String html) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderAddress, senderName);
        helper.setTo(receiverAddress);
        helper.setSubject(subject);

        if (html != null)
            helper.setText(plainText, html);
        else
            helper.setText(plainText);

        mailSender.send(message);
    }

}