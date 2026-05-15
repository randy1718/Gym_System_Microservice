package com.gym.system.workload.service.strategy;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeleteWorkloadStrategy implements WorkloadStrategy{
    private static final Logger logger = LoggerFactory.getLogger(DeleteWorkloadStrategy.class);

    public void execute(Trainer trainer, CalculateTrainerWorkloadRequest request) {
        int duration = request.getTrainingDuration();
        int year = request.getTrainingDate().getYear();
        int month = request.getTrainingDate().getMonthValue();

        Map<Integer, Map<Integer, Integer>> years = trainer.getYears();
        Map<Integer, Integer> months = years.computeIfAbsent(year, y -> new HashMap<>());
        months.merge(month, -duration, Integer::sum);

        if (months.get(month) <= 0) {
            months.remove(month);
        }

        logger.info(
                "Trainer {} updated (Deleted training hours): year={}, month={}, hours={}",
                trainer.getUsername(),
                year,
                month,
                months.getOrDefault(month, 0)
        );
    }
}
