package com.gym.system.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;

@Service
public class AuthService {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    public AuthService(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    @Transactional(readOnly = true)
    public Boolean authenticate(String username, String password) {

        Optional<Trainee> trainee = traineeDAO.findByUsername(username);
        if (trainee.isPresent() && trainee.get().getPassword().equals(password)) {
            return true;
        }

        Optional<Trainer> trainer = trainerDAO.findByUsername(username);
        if (trainer.isPresent() && trainer.get().getPassword().equals(password)) {
            return true;
        }

        throw new IllegalArgumentException("Invalid credentials");
    }

    @Transactional
    public Boolean changeLogin(String username, String oldPassword, String newPassword){
        Optional<Trainee> trainee = traineeDAO.findByUsername(username);
        if (trainee.isPresent() && trainee.get().getPassword().equals(oldPassword)) {
            trainee.get().setPassword(newPassword);
            return true;
        }

        Optional<Trainer> trainer = trainerDAO.findByUsername(username);
        if (trainer.isPresent() && trainer.get().getPassword().equals(oldPassword)) {
            trainer.get().setPassword(newPassword);
            return true;
        }
        throw new IllegalArgumentException("Invalid credentials");
    }
}
