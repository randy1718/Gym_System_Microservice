package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gym.system.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.TraineeController;
import com.gym.system.dto.ActivateDeactivateTraineeRequest;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ActivateDeactivateTraineeControllerTest {
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

        ActivateDeactivateTraineeRequest request = new ActivateDeactivateTraineeRequest();
        request.setUsername("Jonny.Diaz");
        request.setIsActive(false);

        when(facade.activateDeactivateTrainee(Mockito.any()))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(
                patch("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenIsActiveIsMissing() throws Exception {
        ActivateDeactivateTraineeRequest request = new ActivateDeactivateTraineeRequest();
        request.setUsername("Jonny.Diaz");

        mockMvc.perform(
                        patch("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {
        ActivateDeactivateTraineeRequest request = new ActivateDeactivateTraineeRequest();
        request.setUsername("Unknown.User");
        request.setIsActive(true);

        when(facade.activateDeactivateTrainee(Mockito.any()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        patch("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
