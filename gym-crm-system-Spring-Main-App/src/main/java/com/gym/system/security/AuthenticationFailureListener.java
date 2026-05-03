package com.gym.system.security;

import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AuthenticationFailureListener {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    public AuthenticationFailureListener(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    @EventListener
    @Transactional
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {

        String username = event.getAuthentication().getName();

        traineeDAO.findByUsername(username).ifPresent(this::processTrainee);
        trainerDAO.findByUsername(username).ifPresent(this::processTrainer);
    }

    private void processTrainee(Trainee t) {
        t.setFailedAttempts(t.getFailedAttempts() + 1);
        log.warn("Failed login attempt {} for TRAINEE '{}'",
                t.getFailedAttempts(), t.getUsername());

        if (t.getFailedAttempts() >= 3) {
            t.setLockTime(LocalDateTime.now());
            log.error("TRAINEE '{}' LOCKED for 5 minutes due to repeated failures", t.getUsername());
        }
    }

    private void processTrainer(Trainer t) {
        t.setFailedAttempts(t.getFailedAttempts() + 1);
        log.warn("Failed login attempt {} for TRAINER '{}'",
                t.getFailedAttempts(), t.getUsername());

        if (t.getFailedAttempts() >= 3) {
            t.setLockTime(LocalDateTime.now());
            log.error("TRAINER '{}' LOCKED for 5 minutes due to repeated failures", t.getUsername());
        }
    }
}

