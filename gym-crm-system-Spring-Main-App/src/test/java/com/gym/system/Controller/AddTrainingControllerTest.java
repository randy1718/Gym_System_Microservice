package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gym.system.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.TrainingController;
import com.gym.system.dto.AddTrainingRequest;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class AddTrainingControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        TrainingController controller =
                new TrainingController(facade);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturn200_WhenValidRequest() throws Exception {

        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Alex.Perez");
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(60);
        request.setTrainingName("Full Body Workout");

        when(facade.addTraining(Mockito.any(), Mockito.any()))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(
                post("/trainings")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenTraineeUsernameIsMissing() throws Exception {

        AddTrainingRequest request = new AddTrainingRequest();
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(60);
        request.setTrainingName("Full Body Workout");

        mockMvc.perform(
                        post("/trainings")
                                .header("Authorization", "Bearer fake-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenTrainingDurationIsInvalid() throws Exception {

        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Alex.Perez");
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(0);
        request.setTrainingName("Workout");

        mockMvc.perform(
                        post("/trainings")
                                .header("Authorization", "Bearer fake-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {

        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("Ghost.User");
        request.setTrainerUsername("Ana.Lopez");
        request.setTrainingDate("2026-01-08 10:00:00");
        request.setTrainingDuration(60);
        request.setTrainingName("Workout");

        when(facade.addTraining(Mockito.any(), Mockito.any()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        post("/trainings")
                                .header("Authorization", "Bearer fake-token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
