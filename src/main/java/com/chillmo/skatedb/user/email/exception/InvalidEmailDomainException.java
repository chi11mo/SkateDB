package com.chillmo.skatedb.user.email.exception;

public class InvalidEmailDomainException extends RuntimeException {

    private final String email;
    private final String domain;

    public InvalidEmailDomainException(String email) {
        this(email, null);
    }

    public InvalidEmailDomainException(String email, String domain) {
        super(buildMessage(email, domain));
        this.email = email;
        this.domain = domain;
    }

    public String getEmail() {
        return email;
    }

    public String getDomain() {
        return domain;
    }

    private static String buildMessage(String email, String domain) {
        if (domain == null || domain.isBlank()) {
            return "The e-mail address '" + email + "' is not valid or cannot receive mail.";
        }
        return "The e-mail domain '" + domain + "' cannot receive mail (address: " + email + ").";
    }
}
