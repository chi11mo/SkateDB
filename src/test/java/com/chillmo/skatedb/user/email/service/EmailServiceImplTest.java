package com.chillmo.skatedb.user.email.service;

import com.chillmo.skatedb.user.email.exception.InvalidEmailDomainException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailDomainValidator emailDomainValidator;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender, "no-reply@skatedb.test", emailDomainValidator);
    }

    @Test
    void send_whenDomainIsValid_dispatchesMail() throws Exception {
        doNothing().when(emailDomainValidator).validateOrThrow("user@example.com");

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.send("user@example.com", "Subject", "<p>Body</p>");

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void send_whenDomainIsInvalid_throwsExceptionAndSkipsSend() {
        doThrow(new InvalidEmailDomainException("user@invalid.test"))
                .when(emailDomainValidator)
                .validateOrThrow("user@invalid.test");

        assertThatThrownBy(() -> emailService.send("user@invalid.test", "Subject", "<p>Body</p>"))
                .isInstanceOf(InvalidEmailDomainException.class);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void send_whenMailSenderFails_wrapsInIllegalState() throws Exception {
        doNothing().when(emailDomainValidator).validateOrThrow("user@example.com");

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("smtp error")).when(mailSender).send(mimeMessage);

        assertThatThrownBy(() -> emailService.send("user@example.com", "Subject", "<p>Body</p>"))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(MailSendException.class);
    }

    @Test
    void sendAsync_whenValidationFails_doesNotPropagateException() {
        doThrow(new InvalidEmailDomainException("user@invalid.test"))
                .when(emailDomainValidator)
                .validateOrThrow("user@invalid.test");

        assertThatCode(() -> emailService.sendAsync("user@invalid.test", "Subject", "<p>Body</p>"))
                .doesNotThrowAnyException();

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
