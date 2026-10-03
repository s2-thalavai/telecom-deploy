package com.telecom.plans.client;

import com.telecom.plans.config.TelecomProperties;
import com.telecom.plans.dto.TariffPlanResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Pushes new plans to the downstream billing system. The URL differs per environment
 * (stub in qa, real gateway in prod) and the call is protected by Retry + CircuitBreaker,
 * so a billing outage never takes the plans API down.
 */
@Component
public class BillingGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(BillingGatewayClient.class);

    private final TelecomProperties.Billing config;
    private final RestClient restClient;

    public BillingGatewayClient(TelecomProperties props, RestClient.Builder builder) {
        this.config = props.billing();
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout((int) config.timeout().toMillis());
        rf.setReadTimeout((int) config.timeout().toMillis());
        this.restClient = builder.baseUrl(config.baseUrl()).requestFactory(rf).build();
    }

    // Retry is the outer aspect: each attempt is recorded by the circuit breaker; once all
    // attempts fail (or the breaker is open) the fallback runs.
    @Retry(name = "billing", fallbackMethod = "syncFallback")
    @CircuitBreaker(name = "billing")
    public boolean syncPlan(TariffPlanResponse plan) {
        if (!config.enabled()) {
            log.debug("Billing sync disabled in this environment, skipping plan {}", plan.code());
            return true;
        }
        restClient.post().uri("/v1/plans").body(plan).retrieve().toBodilessEntity();
        log.info("Plan {} synced to billing", plan.code());
        return true;
    }

    /** Fallback: plan is saved; a reconciliation job can resync later. */
    public boolean syncFallback(TariffPlanResponse plan, Throwable t) {
        log.warn("Billing unavailable, plan {} queued for later sync: {}", plan.code(), t.toString());
        return false;
    }
}
