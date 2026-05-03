package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gym.system.exception.GlobalExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.TraineeController;
import com.gym.system.dto.DeleteTraineeRequest;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class DeleteTraineeControllerTest {
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
 
        DeleteTraineeRequest request = new DeleteTraineeRequest();
        request.setUsername("Felipe.Ruiz");

        when(facade.deleteTrainee(Mockito.any()))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(
                delete("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {

        DeleteTraineeRequest request = new DeleteTraineeRequest();

        mockMvc.perform(
                        delete("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {

        DeleteTraineeRequest request = new DeleteTraineeRequest();
        request.setUsername("Unknown.User");

        when(facade.deleteTrainee(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        delete("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
