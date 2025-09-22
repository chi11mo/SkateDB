package com.chillmo.skatedb.user.email.service;

import com.chillmo.skatedb.user.email.exception.InvalidEmailDomainException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Locale;

@Component
public class EmailDomainValidator {

    private static final Logger logger = LoggerFactory.getLogger(EmailDomainValidator.class);

    private static final String[] RECORD_TYPES = {"MX", "A", "AAAA"};

    private final DnsLookup dnsLookup;

    /**
     * Creates the validator backed by the default JNDI DNS lookup implementation.
     */
    public EmailDomainValidator() {
        this(new JndiDnsLookup());
    }

    EmailDomainValidator(DnsLookup dnsLookup) {
        this.dnsLookup = dnsLookup;
    }

    /**
     * Validates that the given e-mail address resolves to a domain that can receive mail.
     *
     * @param email address to validate
     * @throws InvalidEmailDomainException if the address is empty, malformed or the domain lacks MX/A/AAAA records
     */
    public void validateOrThrow(String email) {
        if (email == null || email.isBlank()) {
            logger.warn("Skipping e-mail send because the address is blank");
            throw new InvalidEmailDomainException(email);
        }

        String domain = extractDomain(email);
        if (domain == null || domain.isBlank()) {
            logger.warn("Skipping e-mail send because the address '{}' is not a valid e-mail", email);
            throw new InvalidEmailDomainException(email);
        }

        if (!hasValidDnsRecords(domain)) {
            logger.warn(
                    "Skipping e-mail send because the domain '{}' (address: {}) exposes no MX or A/AAAA records",
                    domain,
                    email
            );
            throw new InvalidEmailDomainException(email, domain);
        }
    }

    private String extractDomain(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            String normalized = address.getAddress();
            int atIndex = normalized.lastIndexOf('@');
            if (atIndex < 0 || atIndex == normalized.length() - 1) {
                return null;
            }
            return normalized.substring(atIndex + 1).toLowerCase(Locale.ROOT);
        } catch (AddressException ex) {
            logger.debug("Invalid e-mail syntax for '{}': {}", email, ex.getMessage());
            return null;
        }
    }

    private boolean hasValidDnsRecords(String domain) {
        try {
            Attributes attributes = dnsLookup.lookup(domain);
            if (attributes == null) {
                logger.debug("DNS lookup returned no attributes for domain '{}'", domain);
                return false;
            }

            for (String recordType : RECORD_TYPES) {
                if (hasEntries(attributes, recordType)) {
                    return true;
                }
            }

            logger.debug("Domain '{}' resolved but does not expose MX/A/AAAA records", domain);
            return false;
        } catch (NameNotFoundException | InvalidNameException ex) {
            logger.debug("Domain '{}' is not present in DNS: {}", domain, ex.getMessage());
            return false;
        } catch (CommunicationException ex) {
            logger.info(
                    "DNS lookup for domain '{}' failed due to a communication problem (treating as deliverable): {}",
                    domain,
                    ex.getMessage()
            );
            return true;
        } catch (NamingException ex) {
            logger.info(
                    "DNS lookup for domain '{}' could not be completed (treating as deliverable): {}",
                    domain,
                    ex.getMessage()
            );
            return true;
        }
    }

    private boolean hasEntries(Attributes attributes, String key) {
        if (attributes == null) {
            return false;
        }
        Attribute attribute = attributes.get(key);
        return attribute != null && attribute.size() > 0;
    }

    interface DnsLookup {
        Attributes lookup(String domain) throws NamingException;
    }

    private static final class JndiDnsLookup implements DnsLookup {

        @Override
        public Attributes lookup(String domain) throws NamingException {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");

            try (DirContext ctx = new InitialDirContext(env)) {
                return ctx.getAttributes(domain, RECORD_TYPES);
            }
        }
    }
}
