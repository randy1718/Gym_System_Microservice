package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gym.system.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.LoginController;
import com.gym.system.dto.LoginRequest;

import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class LoginControllerTest {
    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        facade = Mockito.mock(GymServices.class);
        LoginController controller =
                new LoginController(facade);

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
 
        LoginRequest request = new LoginRequest();
        request.setUsername("Felipe.Ruiz");
        request.setPassword("dfslalc123_dvd");

        when(facade.login(Mockito.any()))
                .thenReturn(true);

        // Act + Assert
        mockMvc.perform(
                get("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setPassword("dfslalc123_dvd");

        mockMvc.perform(
                        get("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    void shouldReturn400_WhenPasswordIsMissing() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUsername("Felipe.Ruiz");

        mockMvc.perform(
                        get("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401_WhenCredentialsAreInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setUsername("Felipe.Ruiz");
        request.setPassword("wrongPassword");

        when(facade.login(Mockito.any()))
                .thenReturn(false);

        mockMvc.perform(
                        get("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }
}
