package com.gym.system.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CountTraineesMetric {
    private final Counter traineeCreatedCounter;

    public CountTraineesMetric(MeterRegistry registry) {
        this.traineeCreatedCounter =
                Counter.builder("gym.trainees.created")
                        .description("Number of trainees created")
                        .register(registry);
    }

    public void increment() {
        traineeCreatedCounter.increment();
    }
}


