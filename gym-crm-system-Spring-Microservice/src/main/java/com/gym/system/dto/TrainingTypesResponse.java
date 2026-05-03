package com.gym.system.dto;

import java.util.List;

import com.gym.system.model.TrainingType;

public class TrainingTypesResponse {
    private List<TrainingType> trainingTypes;

    public List<TrainingType> getTrainingTypes() {
        return trainingTypes;
    }

    public void setTrainingTypes(List<TrainingType> trainingTypes) {
        this.trainingTypes = trainingTypes;
    }
}
