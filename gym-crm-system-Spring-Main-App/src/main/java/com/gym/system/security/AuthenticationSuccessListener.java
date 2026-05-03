package com.gym.system.security;

import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthenticationSuccessListener {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    public AuthenticationSuccessListener(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    @EventListener
    @Transactional
    public void onSuccess(AuthenticationSuccessEvent event) {

        String username = event.getAuthentication().getName();

        traineeDAO.findByUsername(username).ifPresent(this::resetTrainee);
        trainerDAO.findByUsername(username).ifPresent(this::resetTrainer);
    }

    private void resetTrainee(Trainee t) {
        t.setFailedAttempts(0);
        t.setLockTime(null);
    }

    private void resetTrainer(Trainer t)
    {
        t.setFailedAttempts(0);
        t.setLockTime(null);
    }
}

