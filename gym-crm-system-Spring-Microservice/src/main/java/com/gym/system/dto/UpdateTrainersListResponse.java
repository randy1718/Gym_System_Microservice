package com.gym.system.dto;

import java.util.List;

public class UpdateTrainersListResponse {
    private List<TraineeTrainersList> updatedTrainers;

    public List<TraineeTrainersList> getUpdatedTrainers() {
        return updatedTrainers;
    }

    public void setUpdatedTrainers(List<TraineeTrainersList> updatedTrainers) {
        this.updatedTrainers = updatedTrainers;
    }
}
