package com.gym.system.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.gym.system.dto.*;
import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.model.Training;
import com.gym.system.model.TrainingType;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;
import com.gym.system.repository.TrainingDAO;
import com.gym.system.repository.TrainingTypeDAO;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TrainingService {

    private final TrainingDAO trainingDAO;
    private final TrainerDAO trainerDAO;
    private final TraineeDAO traineeDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final AuthService AuthService;
    private final WorkloadClient workloadClient;
    private final WorkloadProducer workloadProducer;

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    public TrainingService(TrainingDAO trainingDAO,
                           TrainerDAO trainerDAO,
                           TraineeDAO traineeDAO,
                           TrainingTypeDAO trainingTypeDAO,
                           TrainerService trainerService,
                           TraineeService traineeService,
                           AuthService AuthService,
                           WorkloadClient workloadClient,
                           WorkloadProducer workloadProducer) {
        this.trainingDAO = trainingDAO;
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.AuthService = AuthService;
        this.workloadClient = workloadClient;
        this.workloadProducer = workloadProducer;
    }



    public void create(Training t, String username, String password){

        Boolean isAuthenticated = AuthService.authenticate(username, password);

        if(isAuthenticated){
            logger.info("Service: Creating new training session for trainee {} with trainer {}",
                    t.getTrainee().getId(), t.getTrainer().getId());

            logger.debug("Service: Validating trainer with Username {}", t.getTrainer().getUsername());
            Optional<Trainer> foundTrainer = trainerDAO.findByUsername(t.getTrainer().getUsername());
            if (!foundTrainer.isPresent()) {
                logger.error("Service: Trainer {} does not exist", t.getTrainer().getUsername());
            }

            logger.debug("Service: Validating trainee with Username {}", t.getTrainee().getUsername());
            Optional<Trainee> foundTrainee = traineeDAO.findByUsername(t.getTrainee().getUsername());
            if (!foundTrainee.isPresent()) {
                logger.error("Service: Trainee {} does not exist", t.getTrainee().getUsername());
                throw new IllegalArgumentException("Trainee Username does not exist");
            }

            logger.debug("Service: Validating training type {}", t.getTrainingType().getId());
            Optional<TrainingType> foundTrainingType = trainingTypeDAO.findByName(t.getTrainingType().getName());
            if (!foundTrainingType.isPresent()) {
                logger.error("Service: Training type {} does not exist", t.getTrainingType().getId());
                throw new IllegalArgumentException("Training type does not exist");
            }

            logger.info("Service: All validations passed. Saving training...");
            trainingDAO.save(t);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public Boolean addTraining(AddTrainingRequest request, String token){
        logger.info("Service: Adding training for trainee {} with trainer {}",
                request.getTraineeUsername(), request.getTrainerUsername());

        Optional<Trainer> foundTrainer = trainerDAO.findByUsername(request.getTrainerUsername());
        if (!foundTrainer.isPresent()) {
            logger.error("Service: Trainer {} does not exist", request.getTrainerUsername());
            throw new IllegalArgumentException("Trainer Username does not exist");
        }

        Optional<Trainee> foundTrainee = traineeDAO.findByUsername(request.getTraineeUsername());
        if (!foundTrainee.isPresent()) {
            logger.error("Service: Trainee {} does not exist", request.getTraineeUsername());
            throw new IllegalArgumentException("Trainee Username does not exist");
        }

        Training training = new Training();
        training.setTrainee(foundTrainee.get());
        training.setTrainer(foundTrainer.get());
        training.setTrainingName(request.getTrainingName());
        training.setTrainingType(foundTrainer.get().getSpecialization());
        training.setDate(request.getTrainingDate());
        training.setDuration(request.getTrainingDuration());

        trainingDAO.save(training);

        CalculateTrainerWorkloadRequest workloadRequest = new CalculateTrainerWorkloadRequest();
        workloadRequest.setTrainerUsername(foundTrainer.get().getUsername());
        workloadRequest.setFirstName(foundTrainer.get().getFirstName());
        workloadRequest.setLastName(foundTrainer.get().getLastName());
        workloadRequest.setIsActive(foundTrainer.get().getIsActive());
        workloadRequest.setTrainingDate(training.getDatetime());
        workloadRequest.setTrainingDuration(training.getDuration());
        workloadRequest.setActionType("ADD");

        //workloadClient.sendWorkload(workloadRequest, token);
        workloadProducer.sendWorkload((workloadRequest));

        return true;
    }

    public Boolean deleteTraining(DeleteTrainingRequest request, String token){
        logger.info("Service: Deleting training of trainee username {}", request.getTraineeUsername());
        Trainer trainer = trainerDAO.findByUsername(request.getTrainerUsername())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        Training training = trainingDAO.findByTraineeUsernameAndDate(request.getTraineeUsername(), request.getTrainingDate())
                .orElseThrow(() -> new IllegalArgumentException("Training not found"));

        CalculateTrainerWorkloadRequest workloadRequest = new CalculateTrainerWorkloadRequest();
        workloadRequest.setTrainerUsername(trainer.getUsername());
        workloadRequest.setFirstName(trainer.getFirstName());
        workloadRequest.setLastName(trainer.getLastName());
        workloadRequest.setIsActive(trainer.getIsActive());
        workloadRequest.setTrainingDate(training.getDatetime());
        workloadRequest.setTrainingDuration(training.getDuration());
        workloadRequest.setActionType("DELETE");

        //workloadClient.sendWorkload(workloadRequest, token);
        workloadProducer.sendWorkload((workloadRequest));

        trainingDAO.delete(training.getId());

        return true;
    }

    public Optional<Training> findById(String id){
        logger.info("Service: Fetching training with id {}", id);
        return trainingDAO.findById(id);
    }

    public List<Training> findTrainingsByTraineeUsername(String username, String password, String fromDate, String toDate, String trainerName, String trainingType){
        logger.info("Service: Fetching trainings for trainee {}", username);

        boolean isAuthenticated = traineeService.authenticate(username, password);

        if(isAuthenticated){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime formattedFromDate = LocalDateTime.parse(fromDate, formatter);
            LocalDateTime formattedToDate = LocalDateTime.parse(toDate, formatter);
            if(formattedFromDate.isAfter(formattedToDate)) {
                throw new IllegalArgumentException("Invalid date range");
            }else {
                return trainingDAO.findTrainingsByTraineeUsername(username, fromDate, toDate, trainerName, trainingType);
            }
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public List<Training> findTrainingsByTrainerUsername(String username, String password, String fromDate, String toDate, String traineeName, String trainingType){
        logger.info("Service: Fetching trainings for trainer {}", username);

        boolean isAuthenticated = trainerService.authenticate(username, password);

        if(isAuthenticated){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime formattedFromDate = LocalDateTime.parse(fromDate, formatter);
            LocalDateTime formattedToDate = LocalDateTime.parse(toDate, formatter);
            if(formattedFromDate.isAfter(formattedToDate)) {
                throw new IllegalArgumentException("Invalid date range");
            }else {
                return trainingDAO.findTrainingsByTrainerUsername(username, fromDate, toDate, traineeName, trainingType);
            }
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public List<Trainer> findUnassignedTrainers(String username, String password){
        logger.info("Service: Retrieving unassigned trainers");
        boolean isAuthenticated = traineeService.authenticate(username, password);

        if(isAuthenticated){
            return trainingDAO.findUnassignedTrainers(username);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    public UnassignedTrainersResponse findUnassignedTrainers(UnassignedTrainersRequest request){
        logger.info("Service: Retrieving unassigned trainers");
        List<Trainer> unassignedTrainers = trainingDAO.findUnassignedTrainers(request.getUsername());
        List<UnassignedTrainersList> response = unassignedTrainers.stream()
            .map(trainer -> {
                UnassignedTrainersList dto = new UnassignedTrainersList();
                dto.setUsername(trainer.getUsername());
                dto.setFirstName(trainer.getFirstName());
                dto.setLastName(trainer.getLastName());
                dto.setSpecialization(trainer.getSpecialization().getName());
                return dto;
            })
            .toList();
        UnassignedTrainersResponse responseObj = new UnassignedTrainersResponse();
        responseObj.setUnassignedTrainers(response);
        return responseObj;
    }

    public Optional<Training> findByTraineeUsernameAndDate(String username, String password, String date){
        logger.info("Service: Fetching training for trainee {} on date {}", username, date);
        boolean isAuthenticated = AuthService.authenticate(username, password);

        if(isAuthenticated){
            return trainingDAO.findByTraineeUsernameAndDate(username, date);
        }else{
            throw new IllegalArgumentException("Invalid credentials");
        }
        
    }

    public TraineeTrainingsListResponse getTraineeTrainingsList(TraineeTrainingsListRequest request){
        logger.info("Service: Fetching trainings for trainee {}", request.getUsername());
        Trainee trainee = traineeDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Training> trainings = trainingDAO.findTraineeTrainings(request);
        List<TrainingList> trainingsList = trainings.stream()
        .map(t -> {
            TrainingList dto = new TrainingList();
            dto.setTrainingName(t.getTrainingName());
            dto.setTrainingDate(t.getDate());
            dto.setTrainingType(t.getTrainingType().getName());
            dto.setDuration(t.getDuration());
            dto.setTrainerName(t.getTrainer().getFullName());
            return dto;
        })
         .toList();
        TraineeTrainingsListResponse response = new TraineeTrainingsListResponse();
        response.setTrainings(trainingsList);
        return response;
    }

    public TrainerTrainingsListResponse getTrainerTrainingsList(TrainerTrainingsListRequest request){
        logger.info("Service: Fetching trainings for trainer {}", request.getUsername());
        Trainer trainer = trainerDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Training> trainings = trainingDAO.findTrainerTrainings(request);
        List<TrainerTrainingList> trainingsList = trainings.stream()
        .map(t -> {
            TrainerTrainingList dto = new TrainerTrainingList();
            dto.setTrainingName(t.getTrainingName());
            dto.setTrainingDate(t.getDate());
            dto.setTrainingType(t.getTrainingType().getName());
            dto.setDuration(t.getDuration());
            dto.setTraineeName(t.getTrainee().getFullName());
            return dto;
        })
         .toList();
        TrainerTrainingsListResponse response = new TrainerTrainingsListResponse();
        response.setTrainings(trainingsList);
        return response;
    }

    public List<Training> findAll(){
        logger.info("Service: Fetching all trainings");
        return trainingDAO.findAll();
    }
}
