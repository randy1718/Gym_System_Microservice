package com.gym.system.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import com.gym.system.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.dto.UnassignedTrainersList;
import com.gym.system.dto.UnassignedTrainersRequest;
import com.gym.system.dto.UnassignedTrainersResponse;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class GetUnassignedTrainersControllerTest {
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

        UnassignedTrainersRequest request = new UnassignedTrainersRequest();
        request.setUsername("Camilo.Diaz");

        UnassignedTrainersResponse response = new UnassignedTrainersResponse();
        List<UnassignedTrainersList> unassignedTrainers = new ArrayList<>();
        UnassignedTrainersList ttl1 = new UnassignedTrainersList();
        ttl1.setFirstName("Luis Alberto");
        ttl1.setLastName("Agudelo");
        ttl1.setSpecialization("Cardio");
        ttl1.setUsername("Luis.Agudelo");
        unassignedTrainers.add(ttl1);
        response.setUnassignedTrainers(unassignedTrainers);


        when(facade.getUnassignedTrainers(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                get("/trainers/unassigned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.unassignedTrainers.length()").value(1))
        .andExpect(jsonPath("$.unassignedTrainers[0].firstName").value("Luis Alberto"))
        .andExpect(jsonPath("$.unassignedTrainers[0].lastName").value("Agudelo"))
        .andExpect(jsonPath("$.unassignedTrainers[0].specialization").value("Cardio"))
        .andExpect(jsonPath("$.unassignedTrainers[0].username").value("Luis.Agudelo"));
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {

        UnassignedTrainersRequest request = new UnassignedTrainersRequest();
        // username NOT set

        mockMvc.perform(
                        get("/trainers/unassigned")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenUsernameIsEmpty() throws Exception {

        UnassignedTrainersRequest request = new UnassignedTrainersRequest();
        request.setUsername("");

        mockMvc.perform(
                        get("/trainers/unassigned")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
