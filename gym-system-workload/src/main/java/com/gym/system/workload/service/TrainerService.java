package com.gym.system.workload.service;

import com.gym.system.workload.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final Map<String, Trainer> trainers = new HashMap<>();

    public void calculateWorkload(CalculateTrainerWorkloadRequest request) {

        Trainer trainer = trainers.computeIfAbsent(
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

        int duration = request.getTrainingDuration();
        LocalDate date = request.getTrainingDate();
        String actionType = request.getActionType();

        int year = date.getYear();
        int month = date.getMonthValue();

        Map<Integer, Map<Integer, Integer>> years = trainer.getYears();

        Map<Integer, Integer> months =
                years.computeIfAbsent(year, y -> new HashMap<>());

        if (actionType.equals("ADD")) {
            months.merge(month, duration, Integer::sum);

        } else if (actionType.equals("DELETE")) {
            months.merge(month, -duration, Integer::sum);

            if (months.get(month) <= 0) {
                months.remove(month);
            }
        }

        logger.info("Trainer {} updated: year={}, month={}, hours={}",
                trainer.getUsername(),
                year,
                month,
                months.getOrDefault(month, 0)
        );
    }

    public Map<String, Trainer> getTrainers(){
        return trainers;
    }
}
