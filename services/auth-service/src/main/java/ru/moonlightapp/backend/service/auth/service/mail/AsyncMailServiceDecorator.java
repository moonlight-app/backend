package ru.moonlightapp.backend.service.auth.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public final class AsyncMailServiceDecorator extends MailService {

    private static final ExecutorService MAIL_SENDING_TASKS_POOL = Executors.newVirtualThreadPerTaskExecutor();

    public AsyncMailServiceDecorator(JavaMailSender mailSender) {
        super(mailSender);
    }

    @Override
    public void sendMail(String receiverAddress, String subject, String plainText, String html) {
        MAIL_SENDING_TASKS_POOL.submit(() -> {
            try {
                log.debug("Sending email to '{}'...", receiverAddress);
                super.sendMail(receiverAddress, subject, plainText, html);
                log.info("An email has been sent to '{}'.", receiverAddress);
            } catch (Throwable ex) {
                log.error("Unable to an email!", ex);
            }
        });
    }

}
