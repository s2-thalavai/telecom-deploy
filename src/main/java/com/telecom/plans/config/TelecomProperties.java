package com.telecom.plans.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Type-safe, validated environment configuration. If a required value is missing in an
 * environment, the app FAILS FAST at startup instead of failing on the first request.
 */
@Validated
@ConfigurationProperties(prefix = "telecom")
public record TelecomProperties(
        @NotBlank String environment,
        @Valid @NotNull Billing billing,
        @Valid @NotNull Features features,
        Security security) {

    public record Billing(boolean enabled, @NotBlank String baseUrl, @NotNull Duration timeout) { }

    public record Features(boolean v2ApiEnabled) { }

    /** Only used by dev / qa basic-auth configuration. */
    public record Security(String username, String password) { }
}
