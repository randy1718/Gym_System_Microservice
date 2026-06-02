package com.gym.system.workload.service.strategy;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.logging.TrainerLog;
import com.gym.system.workload.model.TrainerTrainingSummary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeleteWorkloadStrategy implements WorkloadStrategy{

    public void execute(TrainerTrainingSummary trainer, CalculateTrainerWorkloadRequest request) {
        int duration = request.getTrainingDuration();
        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();

        Map<Integer, Map<Integer, Integer>> years = trainer.getYears();
        Map<Integer, Integer> months = years.computeIfAbsent(year, y -> new HashMap<>());
        months.merge(month, -duration, Integer::sum);

        if (months.get(month) <= 0) {
            months.remove(month);
        }

        TrainerLog.trainerWorkloadUpdated(
                trainer,
                year,
                month,
                months.getOrDefault(month, 0),
                "DELETE");
    }
}
