package com.telecom.plans.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * Logs WHAT was deployed WHERE at startup, and exposes the same data on /actuator/info,
 * so pipelines can verify that the expected version reached the expected environment.
 */
@Component
public class EnvironmentStartupReporter implements InfoContributor {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentStartupReporter.class);

    private final Environment env;
    private final TelecomProperties props;
    private final ObjectProvider<BuildProperties> build;

    public EnvironmentStartupReporter(Environment env, TelecomProperties props, ObjectProvider<BuildProperties> build) {
        this.env = env;
        this.props = props;
        this.build = build;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("Started version={} environment={} profiles={} cache={} db={}",
                version(), props.environment(), Arrays.toString(env.getActiveProfiles()),
                env.getProperty("spring.cache.type", "default"),
                maskJdbc(env.getProperty("spring.datasource.url", "n/a")));
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("deployment", Map.of(
                "environment", props.environment(),
                "profiles", Arrays.asList(env.getActiveProfiles()),
                "version", version(),
                "v2ApiEnabled", props.features().v2ApiEnabled()));
    }

    private String version() {
        BuildProperties b = build.getIfAvailable();
        return b != null ? b.getVersion() : "unknown";
    }

    /** jdbc:postgresql://host:5432/db?user=x -> jdbc:postgresql://host:5432/db */
    private static String maskJdbc(String url) {
        int q = url.indexOf('?');
        return q > 0 ? url.substring(0, q) : url;
    }
}
