package com.gym.system.dto;

import jakarta.validation.constraints.NotBlank;

public class TrainerRegistrationRequest {
   @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String specializationName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }
}
