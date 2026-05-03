package com.gym.system.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.system.dto.TrainingTypesResponse;
import com.gym.system.model.TrainingType;
import com.gym.system.repository.TrainingTypeDAO;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TrainingTypeService {

    private final TrainingTypeDAO trainingTypeDAO;

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    public TrainingTypeService(TrainingTypeDAO trainingTypeDAO) { 
        this.trainingTypeDAO = trainingTypeDAO;
    }

    public void create(TrainingType t){
        logger.info("Service: Creating training type {} {}", t.getName());
        trainingTypeDAO.save(t);
    }

    public Optional<TrainingType> findByName(String name){
        logger.info("Service: Finding training type with name {}", name);
            return trainingTypeDAO.findByName(name);
    }

    public List<TrainingType> findAll(){
        logger.info("Service: Retrieving all training Types from Database");
        return trainingTypeDAO.findAll();
    }

    public TrainingTypesResponse getTrainingTypes(){
        logger.info("Service: Getting all training types");
        List<TrainingType> trainingTypes = trainingTypeDAO.findAll();

        TrainingTypesResponse response = new TrainingTypesResponse();
        response.setTrainingTypes(trainingTypes);
        return response;
    }
}
