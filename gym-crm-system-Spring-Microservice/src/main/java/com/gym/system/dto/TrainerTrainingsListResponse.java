package com.gym.system.dto;

import java.util.List;

public class TrainerTrainingsListResponse {
    private List<TrainerTrainingList> trainings;

    public List<TrainerTrainingList> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainerTrainingList> trainings) {
        this.trainings = trainings;
    }
}
