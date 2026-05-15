package com.gym.system.workload.service.strategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkloadStrategyFactory {

    private final Map<String, WorkloadStrategy> strategies;

    public WorkloadStrategyFactory(AddWorkloadStrategy addStrategy,
                                   DeleteWorkloadStrategy deleteStrategy) {
        strategies = Map.of(
                "ADD", addStrategy,
                "DELETE", deleteStrategy
        );
    }

    public WorkloadStrategy getStrategy(String actionType) {

        WorkloadStrategy strategy = strategies.get(actionType);

        if (strategy == null) {
            throw new IllegalArgumentException("Unknown action: " + actionType);
        }

        return strategy;
    }
}
