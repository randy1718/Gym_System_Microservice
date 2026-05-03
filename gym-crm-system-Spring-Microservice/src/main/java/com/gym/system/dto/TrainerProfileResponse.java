package com.gym.system.dto;

import java.util.List;

public class TrainerProfileResponse {
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TrainerTraineesList> trainees;

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    public String getSpecialization(){
        return specialization;
    }

    public void setSpecialization(String specialization){
        this.specialization = specialization;
    }

    public List<TrainerTraineesList> getTrainees(){
        return trainees;
    }

    public void setTrainees(List<TrainerTraineesList> trainees){
        this.trainees = trainees;
    }
}
