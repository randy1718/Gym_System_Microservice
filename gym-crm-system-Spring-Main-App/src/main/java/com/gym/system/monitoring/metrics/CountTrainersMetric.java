package com.gym.system.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CountTrainersMetric {

    private final Counter trainerCreatedCounter;

    public CountTrainersMetric(MeterRegistry registry) {
        this.trainerCreatedCounter =
                Counter.builder("gym.trainers.created")
                        .description("Number of trainers created")
                        .register(registry);
    }

    public void increment() {
        trainerCreatedCounter.increment();
    }
}
