package com.gym.system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.gym.system.dto.TraineeProfileRequest;
import com.gym.system.dto.TraineeProfileResponse;
import com.gym.system.dto.TraineeTrainersList;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class GetTraineeControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        TraineeController controller = new TraineeController(facade);

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
        TraineeProfileRequest request = new TraineeProfileRequest();
        request.setUsername("Julio.Domingo");
        request.setPassword("pass1234566");

        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setFirstName("Julio");
        response.setLastName("Domingo");
        response.setAddress("Calle 12 #34-56");
        LocalDate dob = LocalDate.of(2000, 12, 12);
        response.setDateOfBirth(dob);
        response.setIsActive(true);
        List<TraineeTrainersList> trainers = new ArrayList<>();
        TraineeTrainersList ttl1 = new TraineeTrainersList();
        ttl1.setFirstName("Mario Hernandez");
        trainers.add(ttl1);
        response.setTrainers(trainers);

        when(facade.getTrainee(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                get("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Julio"))
                .andExpect(jsonPath("$.lastName").value("Domingo"))
                .andExpect(jsonPath("$.address").value("Calle 12 #34-56"))
                .andExpect(jsonPath("$.dateOfBirth").value("2000-12-12"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers.length()").value(1));
    }

    @Test
    void shouldReturn400_WhenPasswordIsMissing() throws Exception {

        TraineeProfileRequest request = new TraineeProfileRequest();
        request.setUsername("Julio.Domingo");

        mockMvc.perform(
                        get("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenCredentialsAreInvalid() throws Exception {

        TraineeProfileRequest request = new TraineeProfileRequest();
        request.setUsername("Julio.Domingo");
        request.setPassword("wrongPassword");

        when(facade.getTrainee(Mockito.any()))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(
                        get("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {

        TraineeProfileRequest request = new TraineeProfileRequest();
        request.setUsername("Unknown.User");
        request.setPassword("pass1234566");

        when(facade.getTrainee(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        get("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
