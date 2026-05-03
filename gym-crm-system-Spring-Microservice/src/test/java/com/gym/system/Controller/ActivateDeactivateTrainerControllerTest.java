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
import com.gym.system.controller.TrainerController;
import com.gym.system.dto.ActivateDeactivateTrainerRequest;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ActivateDeactivateTrainerControllerTest {
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
    }

    @Test
    void shouldReturn200_WhenValidRequest() throws Exception {

        ActivateDeactivateTrainerRequest request = new ActivateDeactivateTrainerRequest();
        request.setUsername("Luisa.Vallez");
        request.setIsActive(true);

        when(facade.activateDeactivateTrainer(Mockito.any()))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(
                patch("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenIsActiveIsMissing() throws Exception {
        ActivateDeactivateTrainerRequest request = new ActivateDeactivateTrainerRequest();
        request.setUsername("Jonny.Diaz");

        mockMvc.perform(
                        patch("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTrainerDoesNotExist() throws Exception {
        ActivateDeactivateTrainerRequest request = new ActivateDeactivateTrainerRequest();
        request.setUsername("Unknown.User");
        request.setIsActive(true);

        when(facade.activateDeactivateTrainer(Mockito.any()))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        patch("/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
