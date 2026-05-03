package com.gym.system.dto;

import java.util.List;

public class TraineeTrainingsListResponse {
    private List<TrainingList> trainings;

    public List<TrainingList> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<TrainingList> trainings) {
        this.trainings = trainings;
    }
}
