package com.gym.system.workload.stepDefinitions;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.TrainerTrainingSummary;
import com.gym.system.workload.repository.TrainerTrainingSummaryRepository;
import com.gym.system.workload.service.TrainerService;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrainerWorkloadIntegrationSteps {

    private final TrainerService trainerService;
    private final TrainerTrainingSummaryRepository repository;
    private final JmsTemplate jmsTemplate;

    private CalculateTrainerWorkloadRequest request;
    private Exception exception;

    public TrainerWorkloadIntegrationSteps(TrainerService trainerService, TrainerTrainingSummaryRepository repository, JmsTemplate jmsTemplate) {

        this.trainerService = trainerService;
        this.repository = repository;
        this.jmsTemplate = jmsTemplate;
    }

    @Before
    public void cleanDatabase() {
        repository.deleteAll();
    }

    @Given("trainer {string} does not exist in the DB")
    public void trainerDoesNotExist(String username) {

        request = new CalculateTrainerWorkloadRequest();

        request.setTrainerUsername(username);

        request.setFirstName("John");

        request.setLastName("Doe");

        request.setIsActive(true);

        request.setTrainingDate(LocalDate.of(2026, 1, 10));
    }

    @Given("trainer {string} already has 60 minutes in January 2026")
    public void trainerAlreadyExists(String username) {

        TrainerTrainingSummary trainer = new TrainerTrainingSummary();

        trainer.setUsername(username);

        trainer.setFirstName("John");

        trainer.setLastName("Doe");

        trainer.setIsActive(true);

        trainer.setYears(new HashMap<>());

        trainer.getYears().computeIfAbsent(2026, y -> new HashMap<>()).put(1, 60);

        repository.save(trainer);

        request = new CalculateTrainerWorkloadRequest();

        request.setTrainerUsername(username);

        request.setTrainingDate(LocalDate.of(2026, 1, 10));

        request.setFirstName("John");

        request.setLastName("Doe");

        request.setIsActive(true);
    }

    @When("an ADD workload event of {int} minutes is received")
    public void addWorkload(int duration) {

        request.setTrainingDuration(duration);

        request.setActionType("ADD");

        trainerService.calculateWorkload(request);
    }

    @When("a JMS workload message of {int} minutes is received")
    public void jmsMessageReceived(int duration) throws Exception {

        request.setTrainingDuration(duration);

        request.setActionType("ADD");

        jmsTemplate.convertAndSend("workload.queue", request);

        Thread.sleep(2000);
    }

    @When("a DELETE workload event of {int} minutes is received")
    public void deleteWorkload(int duration) {

        request.setTrainingDuration(duration);
        request.setActionType("DELETE");

        trainerService.calculateWorkload(request);
    }

    @When("an INVALID workload event of {int} minutes is received")
    public void invalidWorkload(int duration) {

        request.setTrainingDuration(duration);
        request.setActionType("INVALID");

        try {
            trainerService.calculateWorkload(request);
        }
        catch (Exception e) {
            exception = e;
        }
    }

    @Then("trainer {string} should exist in MongoDB")
    public void trainerExists(String username) {

        assertTrue(repository.findByUsername(username).isPresent());
    }

    @Then("MongoDB should contain {int} minutes for trainer {string}")
    public void mongoShouldContain(int expected, String username) {

        TrainerTrainingSummary trainer = repository.findByUsername(username).orElseThrow();

        assertEquals(expected, trainer.getYears().get(2026).get(1));
    }

    @Then("trainer {string} should have {int} minutes in January 2026")
    public void trainerShouldHave(String username, int expected) {

        TrainerTrainingSummary trainer = repository.findByUsername(username).orElseThrow();

        assertEquals(expected, trainer.getYears().get(2026).get(1));
    }

    @Then("an integration exception should be thrown")
    public void exceptionThrown() {

        assertNotNull(exception);
    }

    @Then("trainer {string} should not exist in MongoDB")
    public void trainerShouldNotExist(String username) {
        assertFalse(
                repository.findByUsername(username)
                        .isPresent()
        );
    }
}
