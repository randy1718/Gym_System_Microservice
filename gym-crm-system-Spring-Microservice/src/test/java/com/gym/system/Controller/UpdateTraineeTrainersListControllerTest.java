package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.gym.system.controller.TraineeController;
import com.gym.system.dto.TraineeTrainersList;
import com.gym.system.dto.UpdateTrainersListRequest;
import com.gym.system.dto.UpdateTrainersListResponse;
import com.gym.system.dto.UpdatedTrainersList;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class UpdateTraineeTrainersListControllerTest {
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
        UpdateTrainersListRequest request = new UpdateTrainersListRequest();
        request.setUsername("Julio.Domingo");
        List<UpdatedTrainersList> trainers = new ArrayList<>();
        UpdatedTrainersList ttl1 = new UpdatedTrainersList();
        ttl1.setUsername("Carlos.Montoya");
        trainers.add(ttl1);
        request.setTrainers(trainers);

        UpdateTrainersListResponse response = new UpdateTrainersListResponse();
        List<TraineeTrainersList> updatedTrainers = new ArrayList<>();
        TraineeTrainersList uttl1 = new TraineeTrainersList();
        uttl1.setUsername("Carlos.Montoya");
        uttl1.setFirstName("Carlos");
        uttl1.setLastName("Montoya");
        uttl1.setSpecialization("Fitness");
        updatedTrainers.add(uttl1);
        response.setUpdatedTrainers(updatedTrainers);

        when(facade.UpdateTraineeTrainersList(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                put("/trainees/trainersList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedTrainers.length()").value(1))
                .andExpect(jsonPath("$.updatedTrainers[0].firstName").value("Carlos"))
                .andExpect(jsonPath("$.updatedTrainers[0].lastName").value("Montoya"))
                .andExpect(jsonPath("$.updatedTrainers[0].specialization").value("Fitness"))
                .andExpect(jsonPath("$.updatedTrainers[0].username").value("Carlos.Montoya"));
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {
        UpdateTrainersListRequest request = new UpdateTrainersListRequest();
        // username NOT set
        request.setTrainers(new ArrayList<>());

        mockMvc.perform(
                        put("/trainees/trainersList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {
        UpdateTrainersListRequest request = new UpdateTrainersListRequest();
        request.setUsername("Unknown.User");

        UpdatedTrainersList trainer = new UpdatedTrainersList();
        trainer.setUsername("Carlos.Montoya");
        request.setTrainers(List.of(trainer));

        when(facade.UpdateTraineeTrainersList(Mockito.any()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(
                        put("/trainees/trainersList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404_WhenTrainerDoesNotExist() throws Exception {
        // Arrange
        UpdateTrainersListRequest request = new UpdateTrainersListRequest();
        request.setUsername("Julio.Domingo");

        UpdatedTrainersList trainer = new UpdatedTrainersList();
        trainer.setUsername("Non.Existing.Trainer");
        request.setTrainers(List.of(trainer));

        when(facade.UpdateTraineeTrainersList(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainer not found"));

        // Act + Assert
        mockMvc.perform(
                        put("/trainees/trainersList")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }
}
