package com.gym.system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.gym.system.dto.TrainerTraineesList;
import com.gym.system.dto.UpdateTrainerRequest;
import com.gym.system.dto.UpdateTrainerResponse;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class UpdateTrainerControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        TrainerController controller =
                new TrainerController(facade);

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

        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("Alfredo.Cavalli");
        request.setFirstName("Alfredo");
        request.setLastName("Cavalli");
        request.setSpecialization("Cardio");
        request.setIsActive(true);

        UpdateTrainerResponse response = new UpdateTrainerResponse();
        response.setUsername("Alfredo.Cavalli");
        response.setFirstName("Alfredo");
        response.setLastName("Cavalli");
        response.setSpecialization("Cardio");
        response.setIsActive(true);
        List<TrainerTraineesList> traineesList = new ArrayList<>();
        response.setTrainees(traineesList);

        when(facade.updateTrainer(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                put("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {
        // Arrange
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFirstName("Alfredo");
        request.setLastName("Cavalli");
        request.setSpecialization("Cardio");
        request.setIsActive(true);
        // username is missing

        // Act + Assert
        mockMvc.perform(
                        put("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTrainerDoesNotExist() throws Exception {
        // Arrange
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("Non.Existing.Trainer");
        request.setFirstName("Alfredo");
        request.setLastName("Cavalli");
        request.setSpecialization("Cardio");
        request.setIsActive(true);

        when(facade.updateTrainer(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainer not found"));

        // Act + Assert
        mockMvc.perform(
                        put("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404_WhenSpecializationDoesNotExist() throws Exception {
        // Arrange
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setUsername("Alfredo.Cavalli");
        request.setFirstName("Alfredo");
        request.setLastName("Cavalli");
        request.setSpecialization("AlienTraining"); // invalid specialization
        request.setIsActive(true);

        when(facade.updateTrainer(Mockito.any()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Training type not found"));

        // Act + Assert
        mockMvc.perform(
                        put("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
