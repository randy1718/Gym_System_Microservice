package com.gym.system.workload.stepDefinitions;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.TrainerTrainingSummary;
import com.gym.system.workload.repository.TrainerTrainingSummaryRepository;
import com.gym.system.workload.service.TrainerService;
import com.gym.system.workload.service.strategy.AddWorkloadStrategy;
import com.gym.system.workload.service.strategy.DeleteWorkloadStrategy;
import com.gym.system.workload.service.strategy.WorkloadStrategyFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainerWorkloadComponentSteps {

    private TrainerTrainingSummaryRepository repository;

    private TrainerService trainerService;

    private CalculateTrainerWorkloadRequest request;

    private TrainerTrainingSummary trainer;

    private Exception exception;

    @Before
    public void setup() {
        repository = mock(TrainerTrainingSummaryRepository.class);

        WorkloadStrategyFactory strategyFactory = new WorkloadStrategyFactory(new AddWorkloadStrategy(), new DeleteWorkloadStrategy());

        trainerService = new TrainerService(strategyFactory, repository);

        exception = null;
    }

    @Given("trainer {string} does not exist")
    public void trainerDoesNotExist(String username) {
        request = new CalculateTrainerWorkloadRequest();

        request.setTrainerUsername(username);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.of(2026, 1, 10));

        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Given("trainer {string} already has 60 minutes")
    public void trainerAlreadyExists(String username) {
        trainer = new TrainerTrainingSummary();

        trainer.setUsername(username);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setIsActive(true);
        trainer.setYears(new HashMap<>());

        trainer.getYears().computeIfAbsent(2026, y -> new HashMap<>()).put(1, 60);

        when(repository.findByUsername(username)).thenReturn(Optional.of(trainer));

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        request = new CalculateTrainerWorkloadRequest();

        request.setTrainerUsername(username);
        request.setTrainingDate(LocalDate.of(2026, 1, 10));
    }

    @When("an ADD workload event of {int} minutes is processed")
    public void addWorkload(int duration) {

        request.setTrainingDuration(duration);
        request.setActionType("ADD");

        trainerService.calculateWorkload(request);
    }

    @When("a DELETE workload event of {int} minutes is processed")
    public void deleteWorkload(int duration) {

        request.setTrainingDuration(duration);
        request.setActionType("DELETE");

        trainerService.calculateWorkload(request);
    }

    @When("an INVALID workload event of {int} minutes is processed")
    public void invalidWorkload(int duration) {
        request.setTrainingDuration(duration);
        request.setActionType("INVALID");

        try {
            trainerService.calculateWorkload(request);
        } catch (Exception e) {
            exception = e;
        }
    }

    @When("an ADD workload event of {int} minutes is processed for February 2026")
    public void addFebruary(int duration) {
        request.setTrainingDuration(duration);
        request.setTrainingDate(LocalDate.of(2026, 2, 10));

        request.setActionType("ADD");

        trainerService.calculateWorkload(request);
    }

    @When("an ADD workload event of {int} minutes is processed for January 2027")
    public void addNextYear(int duration) {
        request.setTrainingDuration(duration);

        request.setTrainingDate(LocalDate.of(2027, 1, 10));

        request.setActionType("ADD");

        trainerService.calculateWorkload(request);
    }

    @Then("the trainer should have {int} minutes for January 2026")
    public void verifyJanuary(int expected) {
        verify(repository).save(argThat(trainer -> trainer.getYears().get(2026).get(1) == expected));
    }

    @Then("the trainer should not have workload for January 2026")
    public void verifyDeletion() {
        assertFalse(trainer.getYears().get(2026).containsKey(1));
    }

    @Then("the trainer should have {int} minutes for February 2026")
    public void verifyFebruary(int expected) {
        assertEquals(expected, trainer.getYears().get(2026).get(2));
    }

    @Then("the trainer should have {int} minutes for January 2027")
    public void verifyNextYear(int expected) {
        assertEquals(expected, trainer.getYears().get(2027).get(1));
    }

    @Then("an exception should be thrown")
    public void exceptionThrown() {
        assertNotNull(exception);
    }

    @Then("the repository should save the trainer")
    public void repositorySaved() {
        verify(repository).save(any(TrainerTrainingSummary.class));
    }

    @Then("the repository should update the trainer")
    public void repositoryUpdated() {
        verify(repository).save(trainer);
    }
}