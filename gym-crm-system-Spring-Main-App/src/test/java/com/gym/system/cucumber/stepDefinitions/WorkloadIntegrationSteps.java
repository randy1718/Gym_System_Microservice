package com.gym.system.cucumber.stepDefinitions;

import com.gym.system.dto.AddTrainingRequest;
import com.gym.system.dto.TraineeRegistrationRequest;
import com.gym.system.dto.TrainerRegistrationRequest;
import com.gym.system.model.Trainee;
import com.gym.system.model.Trainer;
import com.gym.system.model.Training;
import com.gym.system.model.TrainingType;
import com.gym.system.repository.TraineeDAO;
import com.gym.system.repository.TrainerDAO;
import com.gym.system.repository.TrainingDAO;
import com.gym.system.repository.TrainingTypeDAO;
import com.gym.system.service.TraineeService;
import com.gym.system.service.TrainerService;
import com.gym.system.service.TrainingService;
import com.gym.system.service.TrainingTypeService;
import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
public class WorkloadIntegrationSteps {

    private final TrainingService trainingService;
    private final TrainingTypeDAO trainingTypeDAO;
    private final TrainingDAO trainingDAO;
    private final TrainerDAO trainerDAO;
    private final TraineeDAO traineeDAO;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final JmsTemplate jmsTemplate;
    private final EntityManager em;

    private AddTrainingRequest request;
    private TraineeRegistrationRequest traineeCreationRequest;
    private TrainerRegistrationRequest trainerCreationRequest;
    private CalculateTrainerWorkloadRequest receivedMessage;
    private Exception exception;
    private Boolean result;

    public WorkloadIntegrationSteps(TrainingService trainingService, TrainingDAO trainingDAO, TrainerDAO trainerDAO, TraineeDAO traineeDAO, TrainingTypeDAO trainingTypeDAO, JmsTemplate jmsTemplate, TraineeService traineeService, TrainerService trainerService, TrainingTypeService trainingTypeService, EntityManager em) {
        this.trainingService = trainingService;
        this.trainingDAO = trainingDAO;
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.jmsTemplate = jmsTemplate;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
        this.em = em;
    }

    @Given("a trainer exists in the database")
    public void trainerExists() {

        trainerService.findByUsername("Trainer.Integration")
                .ifPresent(t -> trainerService.delete(t.getUsername()));

        TrainingType trainingType = trainingTypeService.findByName("Cardio")
                .orElseGet(() -> {
                    TrainingType tt = new TrainingType();
                    tt.setName("Cardio");
                    trainingTypeService.create(tt);
                    return tt;
                });

        trainerCreationRequest = new TrainerRegistrationRequest();
        trainerCreationRequest.setFirstName("Trainer");
        trainerCreationRequest.setLastName("Integration");
        trainerCreationRequest.setSpecializationName("Cardio");

        trainerService.create(trainerCreationRequest);
    }

    @Given("a trainee exists in the database")
    public void traineeExists() {

        traineeService.findByUsername("Trainee.Integration")
                .ifPresent(t -> traineeService.delete(t.getUsername()));

        traineeCreationRequest = new TraineeRegistrationRequest();
        traineeCreationRequest.setFirstName("Trainee");
        traineeCreationRequest.setLastName("Integration");

        traineeService.create(traineeCreationRequest);
    }

    @Given("a valid add test training request")
    public void validTrainingRequest() {

        request = new AddTrainingRequest();
        request.setTraineeUsername("Trainee.Integration");
        request.setTrainerUsername("Trainer.Integration");
        request.setTrainingName("Integration Training");
        request.setTrainingDate("2026-01-10 10:00:00");
        request.setTrainingDuration(60);
    }

    @Given("a training request with duration {int}")
    public void trainingRequestWithDuration(int duration) {

        request = new AddTrainingRequest();
        request.setTraineeUsername("Trainee.Integration");
        request.setTrainerUsername("Trainer.Integration");
        request.setTrainingName("Integration Training");
        request.setTrainingDate("2026-01-10 10:00:00");
        request.setTrainingDuration(duration);
    }

    @When("the training is created")
    public void createTraining() throws Exception {

        trainingService.addTraining(request, "fake-token");
        receivedMessage =
                (CalculateTrainerWorkloadRequest)
                        jmsTemplate.receiveAndConvert("workload.queue");

        assertNotNull(receivedMessage);
    }

    @Then("the training should be stored in the database")
    @Transactional
    public void verifyTrainingStored() {
        List<Training> trainings = trainingService.findAll();
        boolean found = trainings.stream().anyMatch(t -> "Integration Training".equals(t.getTrainingName()));
        assertTrue(found);
    }

    @Then("a workload message should be published")
    @Transactional
    public void workloadMessagePublished() {
        assertNotNull(receivedMessage);
    }

    @Then("the workload message should contain the trainer information")
    @Transactional
    public void workloadMessageContainsTrainerInfo() {

        assertEquals("Trainer.Integration", receivedMessage.getTrainerUsername());
        assertEquals("Trainer", receivedMessage.getFirstName());
        assertEquals("Integration", receivedMessage.getLastName());
        assertEquals(60, receivedMessage.getTrainingDuration());
        assertEquals("ADD", receivedMessage.getActionType());
        assertTrue(receivedMessage.getIsActive());
    }


    @Then("the workload duration should be {int}")
    @Transactional
    public void workloadDurationShouldBe(int expectedDuration) {

        assertNotNull(receivedMessage);
        assertEquals(expectedDuration, receivedMessage.getTrainingDuration());
    }

    @After
    public void cleanup() {
        System.out.println("AFTER START");
        List<Training> trainings = trainingService.findAll();

        for (Training t : trainings) {
            if ("Integration Training".equals(t.getTrainingName())) {
                trainingService.delete(t.getId());
            }
        }
        System.out.println("AFTER END");
    }
}