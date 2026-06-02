package com.gym.system.workload.logging;

import com.gym.system.workload.model.TrainerTrainingSummary;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrainerLog {

    private TrainerLog() {
    }

    public static void trainerWorloadSaved(String username){
        log.info(
                "Trainer {} saved in the DB",
                username
        );
    }

    public static void trainerWorkloadUpdated(
            TrainerTrainingSummary trainer,
            int year,
            int month,
            int hours,
            String action
    ) {
        switch (action) {
            case "ADD":
                log.info(
                        "Trainer {} updated workload (hours added): year={}, month={}, hours={}",
                        trainer.getUsername(),
                        year,
                        month,
                        hours
                );
                break;
            case "DELETE":
                log.info(
                        "Trainer {} updated workload (hours deleted): year={}, month={}, hours={}",
                        trainer.getUsername(),
                        year,
                        month,
                        hours
                );
                break;
            default:
                log.info(
                        "Trainer {} updated workload: year={}, month={}, hours={}",
                        trainer.getUsername(),
                        year,
                        month,
                        hours
                );
        }
    }
}