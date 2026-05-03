package com.gym.system.service;

import com.gym.system.config.WebSecurityConfig;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.model.Training;
import com.gym.system.model.TrainingType;
import com.gym.system.util.PasswordGenerator;
import com.gym.system.util.UsernameDuplicates;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class DatabaseInitService {

    @PersistenceContext
    private EntityManager em;

    private final UsernameDuplicates usernameDuplicates;

    private final PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    public DatabaseInitService(UsernameDuplicates usernameDuplicates, PasswordEncoder passwordEncoder) {
        this.usernameDuplicates = usernameDuplicates;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initDatabase(JsonNode rootNode) {

        Long count = em.createQuery(
                "SELECT COUNT(t) FROM Trainee t",
                Long.class).getSingleResult();

        if (count > 0) {
            return;
        }

        JsonNode trainingTypeArray = rootNode.get("trainingTypes");
        JsonNode traineeArray = rootNode.get("trainees");
        JsonNode trainerArray = rootNode.get("trainers");
        JsonNode trainingArray = rootNode.get("trainings");

        if (trainingTypeArray != null && trainingTypeArray.isArray()) {
            for (JsonNode node : trainingTypeArray) {
                TrainingType tt = mapper.convertValue(node, TrainingType.class);
                em.persist(tt);
            }
        }

        if (traineeArray != null && traineeArray.isArray()) {
            for (JsonNode node : traineeArray) {
                Trainee t = mapper.convertValue(node, Trainee.class);

                // ensure required fields
                t.setId(null);
                String username = t.getFirstName() + "." + t.getLastName();
                t.setUsername(usernameDuplicates.generateUniqueUsername(username));
                String rawPassword = PasswordGenerator.generate();
                String hashedPassword = passwordEncoder.encode(rawPassword);
                t.setRawPassword(rawPassword);
                t.setPassword(hashedPassword);
                t.setIsActive(true);

                em.persist(t);
            }
        }

        if (trainerArray != null && trainerArray.isArray()) {
            for (JsonNode node : trainerArray) {
                Trainer t = mapper.convertValue(node, Trainer.class);

                String specializationName = node.get("specializationName").asText();
                String username = t.getFirstName() + "." + t.getLastName();

                t.setId(null);
                TrainingType specialization = em.createQuery(
                        "SELECT tt FROM TrainingType tt WHERE tt.name = :name",
                        TrainingType.class)
                        .setParameter("name", specializationName)
                        .getSingleResult();
                t.setSpecialization(specialization);
                t.setUsername(usernameDuplicates.generateUniqueUsername(username));
                String rawPassword = PasswordGenerator.generate();
                String hashedPassword = passwordEncoder.encode(rawPassword);
                t.setRawPassword(rawPassword);
                t.setPassword(hashedPassword);
                t.setIsActive(true);

                em.persist(t);
            }
        }

        if (trainingArray != null && trainingArray.isArray()) {
            for (JsonNode node : trainingArray) {

                String traineeUsername = node.get("traineeUsername").asText();
                String trainerUsername = node.get("trainerUsername").asText();
                String typeName = node.get("trainingType").asText();

                Trainee trainee = em.createQuery(
                        "SELECT t FROM Trainee t WHERE LOWER(t.username) = LOWER(:username)",
                        Trainee.class)
                        .setParameter("username", traineeUsername)
                        .getSingleResult();

                Trainer trainer = em.createQuery(
                        "SELECT t FROM Trainer t WHERE LOWER(t.username) = LOWER(:username)",
                        Trainer.class)
                        .setParameter("username", trainerUsername)
                        .getSingleResult();

                TrainingType trainingType = em.createQuery(
                        "SELECT tt FROM TrainingType tt WHERE tt.name = :name",
                        TrainingType.class)
                        .setParameter("name", typeName)
                        .getSingleResult();
                Training training = new Training();
                trainee.addTrainer(trainer);
                trainer.addTrainee(trainee);
                training.setTrainee(trainee);
                training.setTrainer(trainer);
                training.setTrainingType(trainingType);
                training.setTrainingName(node.get("trainingName").asText());
                training.setDate(node.get("date").asText());
                training.setDuration(node.get("duration").asInt());

                em.persist(training);
            }
        }
    }
}
