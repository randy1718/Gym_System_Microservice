package com.gym.system.workload.service.strategy;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.TrainerTrainingSummary;

public interface WorkloadStrategy {
    void execute(TrainerTrainingSummary trainer, CalculateTrainerWorkloadRequest request);
}
