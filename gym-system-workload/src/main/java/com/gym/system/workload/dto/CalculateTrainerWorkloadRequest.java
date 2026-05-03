package com.gym.system.workload.dto;

import java.time.LocalDate;

public class CalculateTrainerWorkloadRequest {
    private String trainerUsername;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDate trainingDate;
    private int trainingDuration;
    private String actionType;

    public void setTrainerUsername(String trainerUsername) {this.trainerUsername = trainerUsername;}
    public String getTrainerUsername() {return trainerUsername;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public String getFirstName() {return firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getLastName() {return lastName;}
    public void setIsActive(boolean isActive) {this.isActive = isActive;}
    public boolean getIsActive() {return isActive;}
    public void setTrainingDate(LocalDate trainingDate) {this.trainingDate = trainingDate;}
    public LocalDate getTrainingDate() {return trainingDate;}
    public void setTrainingDuration(int trainingDuration) {this.trainingDuration = trainingDuration;}
    public int getTrainingDuration() {return trainingDuration;}
    public void setActionType(String actionType) {this.actionType = actionType;}
    public String getActionType() {return actionType;}
}
