package com.chillmo.skatedb.user.email.controller;

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
     * Test-Endpoint: Sendet an die angegebene Adresse eine einfache "Guten Tag"-Mail.
     * POST /api/email/test
     * Body: { "email": "adresse@beispiel.de" }
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
     * todo: creating conformation email. To Confirm the registration of a new @User.
     *
     */


}
