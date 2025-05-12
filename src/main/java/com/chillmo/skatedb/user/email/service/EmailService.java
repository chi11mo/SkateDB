package com.chillmo.skatedb.user.email.service;


public interface EmailService {
    /**
     * Versendet eine HTML-E-Mail an die angegebene Adresse.
     *
     * @param to      Empfänger-Adresse
     * @param subject Betreff
     * @param body    HTML-Inhalt der Mail
     */
    void send(String to, String subject, String body);

    void sendAsync(String email, String s, String body);
}