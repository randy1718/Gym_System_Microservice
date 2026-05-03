package com.gym.system.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trainer extends User{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "trainingType_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_trainer_specialization")
    )
    private TrainingType specialization;

    @ManyToMany
    @JoinTable(
        name = "trainer_trainee",
        joinColumns = @JoinColumn(name = "trainer_id"),
        inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees = new ArrayList<>();

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

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

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setRawPassword(String password){
        this.rawPassword = password;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    public TrainingType getSpecialization(){
        return specialization;
    }

    public void setSpecialization(TrainingType specialization){
        this.specialization = specialization;
    }

    public List<Trainee> getTrainees(){
        return trainees;
    }

    public void addTrainee(Trainee trainee){
        this.trainees.add(trainee);
    }

    public void setFailedAttempts(int failedAttempts){
        this.failedAttempts = failedAttempts;
    }

    public int getFailedAttempts(){
        return failedAttempts;
    }

    public void setLockTime(LocalDateTime lockTime){
        this.lockTime = lockTime;
    }

    public boolean isAccountNonLocked() {
        if (this.lockTime == null) return true;
        return lockTime.plusMinutes(5).isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Trainer{" +
            "id='" + id + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", Username='" + username + '\'' +
            ", Password='" + password + '\'' +
            ", IsActive=" + isActive + '\'' +
            ", specialization=" + specialization +
            '}';
    }
}
