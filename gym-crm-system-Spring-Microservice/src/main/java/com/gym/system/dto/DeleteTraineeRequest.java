package com.gym.system.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteTraineeRequest {
    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
