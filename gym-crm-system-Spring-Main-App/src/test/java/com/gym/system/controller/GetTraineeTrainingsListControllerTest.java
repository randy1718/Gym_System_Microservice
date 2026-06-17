package com.gym.system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gym.system.exception.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.dto.TraineeTrainingsListRequest;
import com.gym.system.dto.TraineeTrainingsListResponse;
import com.gym.system.dto.TrainingList;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class GetTraineeTrainingsListControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        TraineeController controller =
                new TraineeController(facade);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldReturn200_WhenValidRequest() throws Exception {

        TraineeTrainingsListRequest request = new TraineeTrainingsListRequest();
        request.setUsername("Rose.Smith");
        request.setFrom(LocalDate.of(2023, 1, 1));
        request.setTo(LocalDate.of(2023, 12, 31));
        request.setTrainerName("John Carter");
        request.setTrainingType("Cardio");

        TraineeTrainingsListResponse response = new TraineeTrainingsListResponse();

        List<TrainingList> trainings = new ArrayList<>();
        response.setTrainings(trainings);

        when(facade.getTraineeTrainingsList(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                get("/trainees/trainingsList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {

        TraineeTrainingsListRequest request = new TraineeTrainingsListRequest();
        request.setFrom(LocalDate.of(2023, 1, 1));
        request.setTo(LocalDate.of(2023, 12, 31));

        mockMvc.perform(
                        get("/trainees/trainingsList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenFromDateIsAfterToDate() throws Exception {

        TraineeTrainingsListRequest request = new TraineeTrainingsListRequest();
        request.setUsername("Rose.Smith");
        request.setFrom(LocalDate.of(2024, 12, 31));
        request.setTo(LocalDate.of(2023, 1, 1));

        when(facade.getTraineeTrainingsList(Mockito.any()))
                .thenThrow(new IllegalArgumentException("Invalid date range"));

        mockMvc.perform(
                        get("/trainees/trainingsList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {

        TraineeTrainingsListRequest request = new TraineeTrainingsListRequest();
        request.setUsername("Unknown.User");

        when(facade.getTraineeTrainingsList(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        get("/trainees/trainingsList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
