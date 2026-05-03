package com.gym.system.monitoring;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("CustomDatabaseHealthIndicator")
public class DatabaseHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        try {
            em.createNativeQuery("SELECT current_database()").getSingleResult();
            return Health.up().withDetail("database", "PostgreSQL OK").build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
