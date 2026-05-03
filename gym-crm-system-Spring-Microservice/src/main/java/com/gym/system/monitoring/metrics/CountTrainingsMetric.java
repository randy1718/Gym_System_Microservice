package com.gym.system.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CountTrainingsMetric {
    private final Counter trainingCreatedCounter;

    public CountTrainingsMetric(MeterRegistry registry) {
        this.trainingCreatedCounter =
                Counter.builder("gym.trainings.created")
                        .description("Number of trainings created")
                        .register(registry);
    }

    public void increment() {
        trainingCreatedCounter.increment();
    }
}
