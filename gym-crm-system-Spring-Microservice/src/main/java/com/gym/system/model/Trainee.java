package com.gym.system.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "trainees")
@PrimaryKeyJoinColumn(name = "id")
public class Trainee extends User {

    @Column
    private LocalDate dateOfBirth;

    @Column
    private String address;

    @OneToMany(
        mappedBy = "trainee",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Training> trainings = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "trainee_trainer",
        joinColumns = @JoinColumn(name = "trainee_id"),
        inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers = new ArrayList<>();

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

    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public List<Trainer> getTrainers(){
        return trainers;
    }

    public void addTrainer(Trainer trainer){
        this.trainers.add(trainer);
    }

    public void setTrainers(List<Trainer> trainers){
        this.trainers = trainers;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public void setFailedAttempts(int failedAttempts){
        this.failedAttempts = failedAttempts;
    }

    public void setLockTime(LocalDateTime lockTime){
        this.lockTime = lockTime;
    }

    public int getFailedAttempts(){
        return failedAttempts;
    }

    public boolean isAccountNonLocked() {
        if (this.lockTime == null) return true;
        return lockTime.plusMinutes(5).isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Trainee{" +
            "id='" + id + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", Username='" + username + '\'' +
            ", Password='" + password + '\'' +
            ", IsActive=" + isActive +
            '}';
    }
}
