package com.chillmo.skatedb.user.email.service;

import com.chillmo.skatedb.user.email.exception.InvalidEmailDomainException;
import org.junit.jupiter.api.Test;

import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailDomainValidatorTest {

    @Test
    void validateOrThrow_whenDnsHasMailRecords_allowsAddress() {
        BasicAttributes attributes = new BasicAttributes(true);
        attributes.put(new BasicAttribute("MX", "10 mail.example.com."));

        EmailDomainValidator validator = new EmailDomainValidator(domain -> attributes);

        assertThatCode(() -> validator.validateOrThrow("user@example.com"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateOrThrow_whenDnsLacksMailRecords_throwsException() {
        BasicAttributes attributes = new BasicAttributes(true);

        EmailDomainValidator validator = new EmailDomainValidator(domain -> attributes);

        assertThatThrownBy(() -> validator.validateOrThrow("user@example.com"))
                .isInstanceOf(InvalidEmailDomainException.class)
                .hasMessageContaining("example.com");
    }

    @Test
    void validateOrThrow_whenDomainMissingInDns_throwsException() {
        EmailDomainValidator validator = new EmailDomainValidator(domain -> {
            throw new NameNotFoundException("missing domain");
        });

        assertThatThrownBy(() -> validator.validateOrThrow("user@missing.test"))
                .isInstanceOf(InvalidEmailDomainException.class)
                .hasMessageContaining("missing.test");
    }

    @Test
    void validateOrThrow_whenDnsLookupFailsDueToCommunicationIssues_allowsAddress() {
        EmailDomainValidator validator = new EmailDomainValidator(domain -> {
            throw new CommunicationException("timeout");
        });

        assertThatCode(() -> validator.validateOrThrow("user@example.com"))
                .doesNotThrowAnyException();
    }
}
