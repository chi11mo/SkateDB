package com.chillmo.skatedb.user.email.service;

import com.chillmo.skatedb.user.email.exception.InvalidEmailDomainException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final EmailDomainValidator emailDomainValidator;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String mailFrom,
            EmailDomainValidator emailDomainValidator
    ) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.emailDomainValidator = emailDomainValidator;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void send(String to, String subject, String body) {
        emailDomainValidator.validateOrThrow(to);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            logger.info("Email successfully sent to: {}", to);
        } catch (MailException e) {
            logger.error("Mail server rejected email to: {}", to, e);
            throw new IllegalStateException("Error sending e-mail to " + to, e);
        } catch (MessagingException e) {
            logger.error("Error sending email to: {}", to, e);
            throw new IllegalStateException("Error sending e-mail to " + to, e);
        }
    }

    @Async
    @Override
    /**
     * {@inheritDoc}
     */
    public void sendAsync(String to, String subject, String body) {
        try {
            send(to, subject, body);
        } catch (InvalidEmailDomainException ex) {
            logger.warn(
                    "Skipping async e-mail because the domain is not deliverable: {} - {}",
                    to,
                    ex.getMessage()
            );
        } catch (RuntimeException ex) {
            logger.error("Unexpected error while sending async e-mail to: {}", to, ex);
        }
    }
}