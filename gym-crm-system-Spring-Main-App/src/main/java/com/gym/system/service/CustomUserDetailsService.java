package com.gym.system.service;

import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    public CustomUserDetailsService(TraineeDAO traineeDAO, TrainerDAO trainerDAO) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        Optional<Trainee> traineeOpt = traineeDAO.findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee t = traineeOpt.get();
            return buildUser(t.getUsername(), t.getPassword(), "TRAINEE", t.isAccountNonLocked());
        }

        Optional<Trainer> trainerOpt = trainerDAO.findByUsername(username);
        if (trainerOpt.isPresent()) {
            Trainer t = trainerOpt.get();
            return buildUser(t.getUsername(), t.getPassword(), "TRAINER", t.isAccountNonLocked());
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }

    private UserDetails buildUser(String username, String password, String role, boolean accountNonLocked) {
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .roles(role)
                .accountLocked(!accountNonLocked)
                .build();
    }
}
