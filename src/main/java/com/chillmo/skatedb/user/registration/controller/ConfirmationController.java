package com.chillmo.skatedb.user.registration.controller;


import com.chillmo.skatedb.user.registration.dto.ConfirmationTokenResponseDto;
import com.chillmo.skatedb.user.registration.service.ConfirmationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class ConfirmationController {
    private final ConfirmationTokenService confirmationTokenService;
    public ConfirmationController(ConfirmationTokenService svc) { this.confirmationTokenService = svc; }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        confirmationTokenService.confirmToken(token);
        return ResponseEntity.ok("E-Mail erfolgreich bestätigt!");
    }

    @PostMapping("/renew")
    public ResponseEntity<ConfirmationTokenResponseDto> renewToken(@RequestParam("token") String expiredToken) {
        var dto = confirmationTokenService.renewToken(expiredToken);
        return ResponseEntity.ok(dto);
    }
}