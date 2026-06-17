package com.gym.system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.gym.system.dto.TrainerProfileRequest;
import com.gym.system.dto.TrainerProfileResponse;
import com.gym.system.dto.TrainerTraineesList;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class GetTrainerControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        TrainerController controller = new TrainerController(facade);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturn200_WhenValidRequest() throws Exception {
        // Arrange
        TrainerProfileRequest request = new TrainerProfileRequest();
        request.setUsername("Mary.Valetyn");
        request.setPassword("passer1234232");

        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstName("Mary");
        response.setLastName("Valetyn");
        response.setIsActive(true);
        response.setSpecialization("Cardio");
        List<TrainerTraineesList> trainees = new ArrayList<>();
        TrainerTraineesList ttl1 = new TrainerTraineesList();
        ttl1.setFirstName("Gustavo Pereira");
        trainees.add(ttl1);
        response.setTrainees(trainees);
        when(facade.getTrainer(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                get("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Mary"))
                .andExpect(jsonPath("$.lastName").value("Valetyn"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.specialization").value("Cardio"))
                .andExpect(jsonPath("$.trainees.length()").value(1));
    }

    @Test
    void shouldReturn400_WhenPasswordIsMissing() throws Exception {

        TrainerProfileRequest request = new TrainerProfileRequest();
        request.setUsername("Mary.Valetyn");

        mockMvc.perform(
                        get("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTrainerDoesNotExist() throws Exception {

        TrainerProfileRequest request = new TrainerProfileRequest();
        request.setUsername("Unknown.Trainer");
        request.setPassword("pass123");

        when(facade.getTrainer(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainer not found"));

        mockMvc.perform(
                        get("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400_WhenCredentialsAreInvalid() throws Exception {

        TrainerProfileRequest request = new TrainerProfileRequest();
        request.setUsername("Mary.Valetyn");
        request.setPassword("wrongPassword");

        when(facade.getTrainer(Mockito.any()))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(
                        get("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
