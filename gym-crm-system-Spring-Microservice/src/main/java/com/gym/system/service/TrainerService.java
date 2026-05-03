package com.gym.system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gym.system.dto.ActivateDeactivateTrainerRequest;
import com.gym.system.dto.TrainerProfileRequest;
import com.gym.system.dto.TrainerProfileResponse;
import com.gym.system.dto.TrainerRegistrationRequest;
import com.gym.system.dto.TrainerRegistrationResponse;
import com.gym.system.dto.TrainerTraineesList;
import com.gym.system.dto.UpdateTrainerRequest;
import com.gym.system.dto.UpdateTrainerResponse;
import com.gym.system.model.Trainer;
import com.gym.system.repository.TrainerDAO;
import com.gym.system.util.PasswordGenerator;
import com.gym.system.util.UsernameDuplicates;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDAO trainerDAO;
    private final TrainingTypeService trainingTypeService;
    private final UsernameDuplicates usernameDuplicates;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TrainerService(TrainerDAO trainerDAO, TrainingTypeService trainingTypeService, UsernameDuplicates usernameDuplicates, PasswordEncoder passwordEncoder) {
        this.trainerDAO = trainerDAO;
        this.trainingTypeService = trainingTypeService;
        this.usernameDuplicates = usernameDuplicates;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String password) {

        Trainer trainer = trainerDAO.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials - trainer"));

        if (!trainer.getIsActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        return passwordEncoder.matches(password, trainer.getPassword());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {

        Trainer trainer = trainerDAO.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAuthenticated = authenticate(username, oldPassword);

        if(isAuthenticated){
            trainer.setPassword(newPassword);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void toggleStatus(String username, String password) {
        Trainer trainer = trainerDAO.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAuthenticated = authenticate(username, password);

        if(isAuthenticated){
            trainerDAO.toggleTrainerStatus(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }   
    }

    public TrainerRegistrationResponse create(TrainerRegistrationRequest tt){
        Trainer t = new Trainer();
        t.setFirstName(tt.getFirstName());
        t.setLastName(tt.getLastName());
        t.setSpecialization(trainingTypeService.findByName(tt.getSpecializationName())
            .orElseThrow(() -> new IllegalArgumentException("Training type not found")));
        String username = tt.getFirstName() + "." + tt.getLastName();
        t.setUsername(usernameDuplicates.generateUniqueUsername(username));
        String rawPassword = PasswordGenerator.generate();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        t.setPassword(hashedPassword);
        t.setRawPassword(rawPassword);
        t.setIsActive(true);
        logger.info("Service: Creating trainer {} {}", t.getFirstName(), t.getLastName());
        trainerDAO.save(t);
        return new TrainerRegistrationResponse(t.getUsername(), t.getPassword());
    }

    public TrainerProfileResponse getTrainerProfile(TrainerProfileRequest request){
        logger.info("Service: Getting profile for trainer {}", request.getUsername());

        Trainer trainer = trainerDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAuthenticated = authenticate(request.getUsername(), request.getPassword());

        if(isAuthenticated) {

            TrainerProfileResponse response = new TrainerProfileResponse();
            response.setFirstName(trainer.getFirstName());
            response.setLastName(trainer.getLastName());
            response.setSpecialization(trainer.getSpecialization().getName());
            response.setIsActive(trainer.getIsActive());
            List<TrainerTraineesList> trainees = trainer.getTrainees().stream()
                    .map(t -> {
                        TrainerTraineesList dto = new TrainerTraineesList();
                        dto.setUsername(t.getUsername());
                        dto.setFirstName(t.getFirstName());
                        dto.setLastName(t.getLastName());
                        return dto;
                    })
                    .toList();
            response.setTrainees(trainees);

            return response;
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public UpdateTrainerResponse updateTrainerProfile(UpdateTrainerRequest request){
        logger.info("Service: Updating profile for trainer {}", request.getUsername());

        Trainer trainer = trainerDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setIsActive(request.getIsActive());

        trainerDAO.update(trainer);

        Trainer updatedTrainer = trainerDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UpdateTrainerResponse response = new UpdateTrainerResponse();
        response.setUsername(updatedTrainer.getUsername());
        response.setFirstName(updatedTrainer.getFirstName());
        response.setLastName(updatedTrainer.getLastName());
        response.setSpecialization(updatedTrainer.getSpecialization().getName());
        response.setIsActive(updatedTrainer.getIsActive());
        List<TrainerTraineesList> trainees = updatedTrainer.getTrainees().stream()
        .map(t -> {
            TrainerTraineesList dto = new TrainerTraineesList();
            dto.setUsername(t.getUsername());
            dto.setFirstName(t.getFirstName());
            dto.setLastName(t.getLastName());
            return dto;
        })
        .toList();
        response.setTrainees(trainees);
        return response;
    }

    public void update(String username, String password, Trainer t){
        logger.info("Service: Updating trainer with id {}", t.getId());

        boolean isAuthenticated = authenticate(username, password);

        if(isAuthenticated){
            trainerDAO.update(t);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public Optional<Trainer> findByUsername(String username, String password){
        logger.info("Service: Finding trainer with username {}", username);

        boolean isAuthenticated = authenticate(username, password);

        if(isAuthenticated){
            return trainerDAO.findByUsername(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }  
    }

    public boolean activateDeactivateTrainer(ActivateDeactivateTrainerRequest request){
        logger.info("Service: Activating/Deactivating trainer {}", request.getUsername());

        Trainer trainer = trainerDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return trainerDAO.activateDeactivateTrainer(trainer.getUsername(), request.getIsActive());
    }

    public List<Trainer> findAll(){
        logger.info("Service: Retrieving all trainers");
        return trainerDAO.findAll();
    }
}
