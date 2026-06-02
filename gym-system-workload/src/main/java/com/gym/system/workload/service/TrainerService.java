package com.gym.system.workload.service;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.TrainerTrainingSummary;
import com.gym.system.workload.repository.TrainerTrainingSummaryRepository;
import com.gym.system.workload.service.strategy.WorkloadStrategy;
import com.gym.system.workload.service.strategy.WorkloadStrategyFactory;
import com.gym.system.workload.logging.TrainerLog;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TrainerService {
    private final WorkloadStrategyFactory strategyFactory;
    private final TrainerTrainingSummaryRepository repository;

    public TrainerService(WorkloadStrategyFactory strategyFactory,  TrainerTrainingSummaryRepository repository) {
        this.strategyFactory = strategyFactory;
        this.repository = repository;
    }

    public void calculateWorkload(CalculateTrainerWorkloadRequest request) {
        TrainerTrainingSummary trainer = getOrCreateTrainer(request);
        WorkloadStrategy strategy = strategyFactory.getStrategy(request.getActionType());
        strategy.execute(trainer, request);
        TrainerTrainingSummary saved = repository.save(trainer);
        TrainerLog.trainerWorloadSaved(saved.getUsername());
    }

    private TrainerTrainingSummary getOrCreateTrainer(CalculateTrainerWorkloadRequest request) {

        return repository.findByUsername(request.getTrainerUsername())
                .orElseGet(() -> createNewTrainer(request));
    }

    private TrainerTrainingSummary createNewTrainer(CalculateTrainerWorkloadRequest request) {
        TrainerTrainingSummary trainer = new TrainerTrainingSummary();

        trainer.setUsername(request.getTrainerUsername());
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setIsActive(request.getIsActive());
        trainer.setYears(new HashMap<>());

        return trainer;
    }
}
