package com.gym.system.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class TrainerTrainingsListRequest {
    @NotBlank
    private String username;

    private LocalDate from;

    private LocalDate to;

    private String traineeName;
    
    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public LocalDate getFrom(){
        return from;
    }

    public void setFrom(LocalDate from){
        this.from = from;
    }

    public LocalDate getTo(){
        return to;
    }

    public void setTo(LocalDate to){
        this.to = to;
    }

    public String getTraineeName(){
        return traineeName;
    }

    public void setTraineeName(String traineeName){
        this.traineeName = traineeName;
    }
}
