package com.gym.system.monitoring;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("CustomJPAHealthIndicator")
public class JPAHealthIndicator implements HealthIndicator {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Health health() {
        try {
            em.createQuery("SELECT 1").getSingleResult();
            return Health.up().withDetail("jpa", "EntityManager OK").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
