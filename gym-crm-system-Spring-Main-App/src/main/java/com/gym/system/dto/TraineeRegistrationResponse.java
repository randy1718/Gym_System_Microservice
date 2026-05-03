package com.gym.system.dto;

public class TraineeRegistrationResponse {
    private String username;
    private String password;

    public TraineeRegistrationResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public TraineeRegistrationResponse() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
