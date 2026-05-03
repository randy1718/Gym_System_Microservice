package com.gym.system.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteTrainingRequest {
    @NotBlank
    private String traineeUsername;

    @NotBlank
    private String trainerUsername;

    @NotBlank
    private String trainingDate;

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getTrainerUsername() {return trainerUsername;}

    public void setTrainerUsername(String trainerUsername) {this.trainerUsername = trainerUsername;}

    public String getTrainingDate() {return trainingDate;}

    public void setTrainingDate(String trainingDate) {this.trainingDate = trainingDate;}

}
