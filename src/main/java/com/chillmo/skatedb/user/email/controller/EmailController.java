package com.chillmo.skatedb.user.email.controller;

import com.chillmo.skatedb.user.email.dto.EmailConfirmationRequestDto;
import com.chillmo.skatedb.user.email.dto.EmailRequestDto;
import com.chillmo.skatedb.user.email.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Test endpoint to send a simple "hello" mail to the provided address.
     * POST /api/email/test
     * Body: { "email": "address@example.com" }
     */
    @PostMapping("/test")
    public ResponseEntity<String> sendTestEmail(@RequestBody EmailRequestDto request) {
        String to = request.getEmail();
        String subject = "Test-Mail von SkateDB";
        String body = "<p>Guten Tag,</p><p>dies ist eine Test-E-Mail von deiner SkateDB-App!</p>";
        emailService.send(to, subject, body);
        return ResponseEntity.ok("Test-Mail an " + to + " versendet.");
    }
    /**
     * Send a confirmation e-mail containing a verification link.
     * POST /api/email/confirm
     * Body: { "email": "address@example.com", "token": "confirmation-token" }
     */
    @PostMapping("/confirm")
    public ResponseEntity<String> sendConfirmationEmail(@RequestBody EmailConfirmationRequestDto request) {
        String to = request.getEmail();
        String token = request.getToken();

        String confirmationLink = "/api/token/confirm?token=" + token;
        String subject = "Bitte bestätige deine E-Mail-Adresse";
        String body = "<p>Hallo,</p>" +
                "<p>bitte bestätige deine E-Mail-Adresse, indem du auf den folgenden Link klickst:</p>" +
                "<a href=\"" + confirmationLink + "\">E-Mail bestätigen</a>";

        emailService.sendAsync(to, subject, body);

        return ResponseEntity.ok("Bestätigungsmail an " + to + " versendet.");
    }


}
