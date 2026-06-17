package com.gym.system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.dto.TraineeRegistrationRequest;
import com.gym.system.dto.TraineeRegistrationResponse;
import com.gym.system.exception.GlobalExceptionHandler;
import com.gym.system.service.GymServices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class CreateTraineeControllerTest {

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
    }

    @Test
    void shouldReturn200_WhenValidRequest() throws Exception {
        // Arrange
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Lucas");
        request.setLastName("Diaz");

        TraineeRegistrationResponse response = new TraineeRegistrationResponse();
        response.setUsername("Lucas.Diaz");
        response.setPassword("Abc1234567");

        when(facade.createTrainee(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("Lucas.Diaz"))
        .andExpect(jsonPath("$.password").value("Abc1234567"));
    }

    @Test
    void shouldReturn400_WhenFirstNameIsMissing() throws Exception {
        // Arrange
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setLastName("Diaz"); // firstName missing

        // Act + Assert
        mockMvc.perform(
                        post("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(
                        post("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500_WhenUnexpectedErrorOccurs() throws Exception {
        // Arrange
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Lucas");
        request.setLastName("Diaz");

        when(facade.createTrainee(Mockito.any()))
                .thenThrow(new RuntimeException("DB down"));

        // Act + Assert
        mockMvc.perform(
                        post("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError());
    }
}
