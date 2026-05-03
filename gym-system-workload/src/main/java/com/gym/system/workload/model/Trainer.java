package com.gym.system.workload.model;

import java.util.HashMap;
import java.util.Map;

public class Trainer {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    private Map<Integer, Map<Integer, Integer>> years = new HashMap<>();

    public void setUsername(String username){this.username = username;}
    public String getUsername(){return username;}
    public void setFirstName(String firstName){ this.firstName = firstName;}
    public String getFirstName(){return firstName;}
    public void setLastName(String lastName){ this.lastName = lastName;}
    public String getLastName(){return lastName;}
    public void setIsActive(boolean isActive){this.isActive = isActive;}
    public boolean IsActive(){return isActive;}
    public void setYears(Map<Integer, Map<Integer, Integer>> years){ this.years = years;}
    public Map<Integer, Map<Integer, Integer>> getYears(){return years;}
}


