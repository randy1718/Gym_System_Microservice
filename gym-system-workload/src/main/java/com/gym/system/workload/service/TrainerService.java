package com.gym.system.workload.service;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.Trainer;
import com.gym.system.workload.service.strategy.WorkloadStrategy;
import com.gym.system.workload.service.strategy.WorkloadStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final Map<String, Trainer> trainers = new HashMap<>();
    private final WorkloadStrategyFactory strategyFactory;

    public TrainerService(WorkloadStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public void calculateWorkload(CalculateTrainerWorkloadRequest request) {
        Trainer trainer = getOrCreateTrainer(request);
        WorkloadStrategy strategy = strategyFactory.getStrategy(request.getActionType());
        strategy.execute(trainer, request);
    }

    private Trainer getOrCreateTrainer(CalculateTrainerWorkloadRequest request) {
        return trainers.computeIfAbsent(
                request.getTrainerUsername(),
                username -> {
                    Trainer t = new Trainer();
                    t.setUsername(username);
                    t.setFirstName(request.getFirstName());
                    t.setLastName(request.getLastName());
                    t.setIsActive(request.getIsActive());
                    t.setYears(new HashMap<>());
                    return t;
                }
        );
    }

    public Map<String, Trainer> getTrainers(){
        return trainers;
    }
}
