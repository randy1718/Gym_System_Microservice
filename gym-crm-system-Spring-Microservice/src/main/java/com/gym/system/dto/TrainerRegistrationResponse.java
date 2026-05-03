package com.gym.system.dto;

public class TrainerRegistrationResponse {
    private String username;
    private String password;

    public TrainerRegistrationResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public TrainerRegistrationResponse() {
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
