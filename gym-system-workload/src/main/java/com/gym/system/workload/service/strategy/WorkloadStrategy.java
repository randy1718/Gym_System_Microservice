package com.gym.system.workload.service.strategy;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.Trainer;

public interface WorkloadStrategy {
    void execute(Trainer trainer, CalculateTrainerWorkloadRequest request);
}
