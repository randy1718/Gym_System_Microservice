package com.gym.system.monitoring.metrics;

import com.gym.system.repository.UserDAO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CountActiveUsersMetric {

    CountActiveUsersMetric(MeterRegistry registry, UserDAO userDAO) {
        Gauge.builder("gym.users.active", this, s -> userDAO.countActiveUsers())
                .description("Number of active users (trainees + trainers)")
                .register(registry);
    }
}
