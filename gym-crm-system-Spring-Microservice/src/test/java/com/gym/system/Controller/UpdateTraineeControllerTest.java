package com.gym.system.Controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.gym.system.controller.TraineeController;
import com.gym.system.dto.TraineeTrainersList;
import com.gym.system.dto.UpdateTraineeRequest;
import com.gym.system.dto.UpdateTraineeResponse;
import com.gym.system.service.GymServices;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class UpdateTraineeControllerTest {
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
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldReturn200_WhenValidRequest() throws Exception {

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("Roberto.Cavalli");
        request.setFirstName("Roberto");
        request.setLastName("Cavalli");
        //request.setAddress("Street 5th 12 30");
        request.setIsActive(true);
        //request.setDateOfBirth("2000-09-19");

        UpdateTraineeResponse response = new UpdateTraineeResponse();
        response.setUsername("Roberto.Cavalli");
        response.setFirstName("Roberto");
        response.setLastName("Cavalli");
        response.setAddress("Street 5th 12 30");
        response.setIsActive(true);
        LocalDate dob = LocalDate.of(2000, 9, 19);
        response.setDateOfBirth(dob);
        List<TraineeTrainersList> trainerLists = new ArrayList<>();
        response.setTrainers(trainerLists); 

        when(facade.updateTrainee(Mockito.any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                put("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400_WhenUsernameIsMissing() throws Exception {

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("Roberto");
        request.setLastName("Cavalli");
        request.setIsActive(true);

        mockMvc.perform(
                        put("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenFirstNameIsMissing() throws Exception {

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("Roberto.Cavalli");
        request.setLastName("Cavalli");
        request.setIsActive(true);

        mockMvc.perform(
                        put("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_WhenTraineeDoesNotExist() throws Exception {

        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setUsername("Unknown.User");
        request.setFirstName("Roberto");
        request.setLastName("Cavalli");
        request.setIsActive(true);

        when(facade.updateTrainee(Mockito.any()))
                .thenThrow(new EntityNotFoundException("Trainee not found"));

        mockMvc.perform(
                        put("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400_WhenDateFormatIsInvalid() throws Exception {

        String invalidJson = """
        {
          "username": "Roberto.Cavalli",
          "firstName": "Roberto",
          "lastName": "Cavalli",
          "isActive": true,
          "dateOfBirth": "19-09-2000"
        }
        """;

        mockMvc.perform(
                        put("/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(status().isBadRequest());
    }


}
