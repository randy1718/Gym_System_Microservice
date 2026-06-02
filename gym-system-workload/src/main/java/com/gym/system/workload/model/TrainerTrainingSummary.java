package com.gym.system.workload.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "trainer_training_summary")
@CompoundIndex(
        name = "username_idx",
        def = "{'username': 1}"
)
public class TrainerTrainingSummary {

    @Id
    private String username;

    private String firstName;

    private String lastName;

    private Boolean isActive;

    private Map<Integer, Map<Integer, Integer>> years;

    public void setUsername(String username) { this.username = username;}

    public String getUsername(){ return this.username;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getFirstName(){return this.firstName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getLastName(){return this.lastName;}

    public void setIsActive(Boolean isActive) {this.isActive = isActive;}

    public Boolean IsActive(){return this.isActive;}

    public void setYears(Map<Integer, Map<Integer, Integer>> years) {this.years = years;}

    public Map<Integer, Map<Integer, Integer>> getYears(){return years;}
}