package com.gym.system.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UpdateTrainersListRequest {
    @NotBlank
    private String username;

    @NotEmpty
    private List<UpdatedTrainersList> trainers;
    
    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public List<UpdatedTrainersList> getTrainers(){
        return trainers;
    }

    public void setTrainers(List<UpdatedTrainersList> trainers){
        this.trainers = trainers;
    }
}
