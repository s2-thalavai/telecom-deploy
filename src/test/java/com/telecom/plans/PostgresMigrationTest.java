package com.telecom.plans;

import com.telecom.plans.repository.TariffPlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the SAME Flyway migrations against the SAME Postgres major version as prod,
 * catching H2-vs-Postgres differences before deployment. Skipped when Docker is absent.
 */
@SpringBootTest(properties = {
        "spring.flyway.locations=classpath:db/migration",
        "spring.cache.type=caffeine",
        "management.health.redis.enabled=false",
        "telecom.billing.enabled=false",
        // qa placeholders normally injected by the pipeline / K8s secrets
        "DB_USERNAME=unused", "DB_PASSWORD=unused", "API_PASSWORD=test"})
@ActiveProfiles("qa")
@Testcontainers(disabledWithoutDocker = true)
class PostgresMigrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    TariffPlanRepository repository;

    @Test
    void schemaMigratesAndJpaValidates() {
        assertThat(repository.count()).isZero();   // context started => ddl-auto=validate passed
    }
}
