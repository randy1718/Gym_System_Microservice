package com.gym.system.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.TrainingController;
import com.gym.system.dto.AddTrainingRequest;
import com.gym.system.exception.GlobalExceptionHandler;
import com.gym.system.service.GymServices;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingControllerSteps {

    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    private AddTrainingRequest request;
    private MvcResult result;

    @Before
    public void setup() {

        facade = Mockito.mock(GymServices.class);

        TrainingController controller =
                new TrainingController(facade);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        request = new AddTrainingRequest();
    }

    //////////////////////////////////////////////////////
    // ADD TRAINING
    //////////////////////////////////////////////////////

    @Given("a valid add training request")
    public void validAddTrainingRequest() {

        request.setTraineeUsername("Alex.Perez");
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(60);
        request.setTrainingName("Full Body Workout");

        when(facade.addTraining(any(), anyString()))
                .thenReturn(true);
    }

    @Given("an add training request without trainee username")
    public void addTrainingWithoutTraineeUsername() {

        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(60);
        request.setTrainingName("Full Body Workout");
    }

    @Given("an add training request with invalid duration")
    public void addTrainingWithInvalidDuration() {

        request.setTraineeUsername("Alex.Perez");
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(0);
        request.setTrainingName("Workout");
    }

    @Given("an add training request for a non existing trainee")
    public void addTrainingForNonExistingTrainee() {

        request.setTraineeUsername("Ghost.User");
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(60);
        request.setTrainingName("Workout");

        when(facade.addTraining(any(), anyString()))
                .thenThrow(
                        new EntityNotFoundException(
                                "Trainee not found"));
    }

    @When("the client adds the training")
    public void clientAddsTraining()
            throws Exception {

        result = mockMvc.perform(
                        post("/trainings")
                                .header(
                                        "Authorization",
                                        "Bearer fake-token")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andReturn();
    }

    @Then("the training response status should be {int}")
    public void trainingResponseStatus(
            int status) {

        assertEquals(
                status,
                result.getResponse().getStatus());
    }
}