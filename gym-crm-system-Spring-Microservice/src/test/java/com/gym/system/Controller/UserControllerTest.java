package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.gym.system.controller.UserController;
import com.gym.system.dto.ChangeLoginRequest;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class UserControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        UserController controller =
                new UserController(facade);

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

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Fabian.Gomez");
        request.setOldPassword("Fagobian18");
        request.setNewPassword("dfslalc123_dvd");

        when(facade.changeLogin(Mockito.any()))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(
                put("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenOldPasswordIsMissing() throws Exception {

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Fabian.Gomez");
        request.setNewPassword("NewPass123!");

        mockMvc.perform(
                        put("/users/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenNewPasswordIsMissing() throws Exception {

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Fabian.Gomez");
        request.setOldPassword("OldPass123");

        mockMvc.perform(
                        put("/users/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenOldPasswordIsIncorrect() throws Exception {

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Fabian.Gomez");
        request.setOldPassword("WrongPassword");
        request.setNewPassword("NewPass123!");

        when(facade.changeLogin(Mockito.any()))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(
                        put("/users/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenUserDoesNotExist() throws Exception {

        ChangeLoginRequest request = new ChangeLoginRequest();
        request.setUsername("Unknown.User");
        request.setOldPassword("OldPass123");
        request.setNewPassword("NewPass123!");

        when(facade.changeLogin(Mockito.any()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(
                        put("/users/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
