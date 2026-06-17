package com.gym.system.service;

import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gym.system.dto.ActivateDeactivateTraineeRequest;
import com.gym.system.dto.DeleteTraineeRequest;
import com.gym.system.dto.TraineeProfileRequest;
import com.gym.system.dto.TraineeProfileResponse;
import com.gym.system.dto.TraineeRegistrationRequest;
import com.gym.system.dto.TraineeRegistrationResponse;
import com.gym.system.dto.TraineeTrainersList;
import com.gym.system.dto.UpdateTraineeRequest;
import com.gym.system.dto.UpdateTraineeResponse;
import com.gym.system.dto.UpdateTrainersListRequest;
import com.gym.system.dto.UpdateTrainersListResponse;
import com.gym.system.dto.UpdatedTrainersList;
import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;
import com.gym.system.util.PasswordGenerator;


import com.gym.system.util.UsernameDuplicates;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final UsernameDuplicates usernameDuplicates;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public  TraineeService(TraineeDAO traineeDAO, TrainerDAO trainerDAO, UsernameDuplicates usernameDuplicates,  PasswordEncoder passwordEncoder) {
        this.traineeDAO = traineeDAO;
        this.trainerDAO = trainerDAO;
        this.usernameDuplicates = usernameDuplicates;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String username, String password) {

        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        if (!trainee.getIsActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        return passwordEncoder.matches(password, trainee.getPassword());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {

        Trainee trainee = traineeDAO.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Boolean isAuthenticated = authenticate(username, oldPassword);

        if(isAuthenticated){
            trainee.setPassword(newPassword);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void toggleStatus(String username, String password) {
        Trainee trainee = traineeDAO.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Boolean isAuthenticated = authenticate(username, password);
        
        if(isAuthenticated){
            traineeDAO.toggleTraineeStatus(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public TraineeRegistrationResponse  create(TraineeRegistrationRequest tt){
        Trainee t = new Trainee();
        t.setFirstName(tt.getFirstName());
        t.setLastName(tt.getLastName());
        t.setDateOfBirth(tt.getDateOfBirth());
        t.setAddress(tt.getAddress());
        String username = tt.getFirstName() + "." + tt.getLastName();
        t.setUsername(usernameDuplicates.generateUniqueUsername(username));
        String rawPassword = PasswordGenerator.generate();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        t.setPassword(hashedPassword);
        t.setRawPassword(rawPassword);
        t.setIsActive(true);
        logger.info("Service: Creating trainee {} {}", t.getFirstName(), t.getLastName());
        traineeDAO.save(t);
        return new TraineeRegistrationResponse(t.getUsername(), t.getPassword());
    }

    public void update(String username, String password, Trainee t){
        logger.info("Service: Updating trainee with id {}", t.getId());
        
        Boolean isAuthenticated = authenticate(username, password);

        if(isAuthenticated){
            traineeDAO.update(t);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void updateTrainersList(Trainer trainer, String traineeUsername, String traineePassword){
        logger.info("Service: Updating trainers list for trainee {}", traineeUsername);

        Boolean isAuthenticated = authenticate(traineeUsername, traineePassword);

        if(isAuthenticated){
            traineeDAO.updateTrainersList(trainer, traineeUsername);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void delete(String username, String password){
        logger.info("Service: Deleting trainee with username {}", username);
        Trainee trainee = traineeDAO.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Boolean isAuthenticated = authenticate(username, password);
        
        if(isAuthenticated){
            traineeDAO.delete(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public void delete(String username){
        logger.info("Service: Deleting trainee with username {}", username);
        Trainee trainee = traineeDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        traineeDAO.delete(trainee.getUsername());
    }

    public boolean toggleTraineeStatus(String username, String password) {

        Boolean isAuthenticated = authenticate(username, password);
        
        if(isAuthenticated){
            return traineeDAO.toggleTraineeStatus(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public Optional<Trainee> findByUsername(String username, String password){
        logger.info("Service: Finding trainee with username {}", username);

        Boolean isAuthenticated = authenticate(username, password);
        
        if(isAuthenticated){
            return traineeDAO.findByUsername(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public Optional<Trainee> findByUsername(String username){
        logger.info("Service: Finding trainee with username {}", username);
            return traineeDAO.findByUsername(username);
    }

    public Boolean deleteTraineeProfile(DeleteTraineeRequest request){
        logger.info("Service: Deleting trainee with username {}", request.getUsername());
        Trainee trainee = traineeDAO.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        traineeDAO.delete(request.getUsername());

        return true;
    }

    public TraineeProfileResponse getTraineeProfile(TraineeProfileRequest request){
        logger.info("Service: Getting profile for trainee {}", request.getUsername());

        Trainee trainee = traineeDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAuthenticated = authenticate(request.getUsername(), request.getPassword());

        if(isAuthenticated) {
            TraineeProfileResponse response = new TraineeProfileResponse();
            response.setFirstName(trainee.getFirstName());
            response.setLastName(trainee.getLastName());
            response.setDateOfBirth(trainee.getDateOfBirth());
            response.setAddress(trainee.getAddress());
            response.setIsActive(trainee.getIsActive());
            List<TraineeTrainersList> trainers = trainee.getTrainers().stream()
                    .map(t -> {
                        TraineeTrainersList dto = new TraineeTrainersList();
                        dto.setUsername(t.getUsername());
                        dto.setFirstName(t.getFirstName());
                        dto.setLastName(t.getLastName());
                        dto.setSpecialization(t.getSpecialization().getName());
                        return dto;
                    })
                    .toList();
            response.setTrainers(trainers);

            return response;
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public UpdateTraineeResponse updateTraineeProfile(UpdateTraineeRequest request){
        logger.info("Service: Updating profile for trainee {}", request.getUsername());

        Trainee trainee = traineeDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        if(request.getDateOfBirth() != null) trainee.setDateOfBirth(request.getDateOfBirth());
        if(request.getAddress() != null) trainee.setAddress(request.getAddress());
        trainee.setIsActive(request.getIsActive());

        traineeDAO.update(trainee);

        Trainee updatedTrainee = traineeDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UpdateTraineeResponse response = new UpdateTraineeResponse();
        response.setUsername(updatedTrainee.getUsername());
        response.setFirstName(updatedTrainee.getFirstName());
        response.setLastName(updatedTrainee.getLastName());
        response.setDateOfBirth(updatedTrainee.getDateOfBirth());
        response.setAddress(updatedTrainee.getAddress());
        response.setIsActive(updatedTrainee.getIsActive());
        List<TraineeTrainersList> trainers = updatedTrainee.getTrainers().stream()
        .map(t -> {
            TraineeTrainersList dto = new TraineeTrainersList();
            dto.setUsername(t.getUsername());
            dto.setFirstName(t.getFirstName());
            dto.setLastName(t.getLastName());
            dto.setSpecialization(t.getSpecialization().getName());
            return dto;
        })
        .toList();
        response.setTrainers(trainers);

        return response;
    }

    public UpdateTrainersListResponse UpdateTraineeTrainersList(UpdateTrainersListRequest request){
        logger.info("Service: Updating trainers list for trainee {}", request.getUsername());

        Trainee trainee = traineeDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UpdatedTrainersList> trainersToAdd = request.getTrainers();
        List<Trainer> updatedTrainers = new ArrayList<>();
        for(UpdatedTrainersList updatedTrainer : trainersToAdd){
            Trainer trainer = trainerDAO.findByUsername(updatedTrainer.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));
            updatedTrainers.add(trainer);
        }
        traineeDAO.updateTraineeTrainersList(trainee.getUsername(), updatedTrainers);
        UpdateTrainersListResponse response = new UpdateTrainersListResponse();
        List<TraineeTrainersList> updatedTrainersList = trainee.getTrainers().stream()
        .map(t -> {
            TraineeTrainersList dto = new TraineeTrainersList();
            dto.setUsername(t.getUsername());
            dto.setFirstName(t.getFirstName());
            dto.setLastName(t.getLastName());
            dto.setSpecialization(t.getSpecialization().getName());
            return dto;
        })
         .toList();
        response.setUpdatedTrainers(updatedTrainersList);
        return response;
    }

    public Boolean activateDeactivateTrainee(ActivateDeactivateTraineeRequest request){
        logger.info("Service: Activating/Deactivating trainee {}", request.getUsername());

        Trainee trainee = traineeDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return traineeDAO.activateDeactivateTrainee(trainee.getUsername(), request.getIsActive());
    }

    public List<Trainee> findAll(){
        logger.info("Service: Retrieving all trainees");
        return traineeDAO.findAll();
    }
}
