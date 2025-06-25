package com.chillmo.skatedb.user.email.service;


public interface EmailService {
    /**
     * Send an HTML e-mail to the given address.
     *
     * @param to      recipient address
     * @param subject mail subject
     * @param body    HTML body of the mail
     */
    void send(String to, String subject, String body);

    /**
     * Send the e-mail asynchronously using the configured executor.
     */
    void sendAsync(String email, String subject, String body);
}