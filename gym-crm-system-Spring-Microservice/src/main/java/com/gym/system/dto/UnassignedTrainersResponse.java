package com.gym.system.dto;

import java.util.List;

public class UnassignedTrainersResponse {
    private List<UnassignedTrainersList> unassignedTrainers;

    public List<UnassignedTrainersList> getUnassignedTrainers() {
        return unassignedTrainers;
    }
    
    public void setUnassignedTrainers(List<UnassignedTrainersList> unassignedTrainers) {
        this.unassignedTrainers = unassignedTrainers;
    }
}