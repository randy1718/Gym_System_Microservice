package com.gym.system.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.system.controller.TraineeController;
import com.gym.system.dto.TraineeRegistrationRequest;
import com.gym.system.dto.TraineeRegistrationResponse;
import com.gym.system.dto.UpdateTraineeRequest;
import com.gym.system.dto.UpdateTraineeResponse;
import com.gym.system.dto.TraineeProfileRequest;
import com.gym.system.dto.TraineeProfileResponse;
import com.gym.system.dto.TraineeTrainersList;
import com.gym.system.dto.ActivateDeactivateTraineeRequest;
import com.gym.system.dto.DeleteTraineeRequest;
import com.gym.system.dto.TraineeTrainingsListRequest;
import com.gym.system.dto.TraineeTrainingsListResponse;
import com.gym.system.dto.TrainingList;
import com.gym.system.dto.UpdateTrainersListRequest;
import com.gym.system.dto.UpdateTrainersListResponse;
import com.gym.system.dto.UpdatedTrainersList;
import com.gym.system.dto.TraineeTrainersList;
import com.gym.system.exception.GlobalExceptionHandler;
import com.gym.system.service.GymServices;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TraineeControllerSteps {

    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    private TraineeProfileRequest profileRequest;
    private TraineeRegistrationRequest request;
    private UpdateTraineeRequest updateRequest;
    private ActivateDeactivateTraineeRequest activateRequest;
    private DeleteTraineeRequest deleteRequest;
    private TraineeTrainingsListRequest traineeTrainingsRequest;
    private UpdateTrainersListRequest updateTrainersListRequest;

    private String rawJson;
    private MvcResult result;

    @Before
    public void setup() {

        facade = Mockito.mock(GymServices.class);

        TraineeController controller = new TraineeController(facade);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        request = new TraineeRegistrationRequest();
    }

    //////////////////////////////////////////////////////
    // CREATE TRAINEE

    /// ///////////////////////////////////////////////////

    @Given("a valid trainee registration request")
    public void validRequest() {

        request.setFirstName("Lucas");
        request.setLastName("Diaz");

        TraineeRegistrationResponse response = new TraineeRegistrationResponse();

        response.setUsername("Lucas.Diaz");
        response.setPassword("Abc1234567");

        when(facade.createTrainee(any())).thenReturn(response);
    }

    @Given("a trainee request without first name")
    public void missingFirstName() {

        request.setLastName("Diaz");
    }

    @Given("an empty trainee request")
    public void emptyRequest() {

        request = new TraineeRegistrationRequest();
    }

    @Given("the trainee service is unavailable")
    public void serviceUnavailable() {

        when(facade.createTrainee(any())).thenThrow(new RuntimeException("DB down"));
    }

    @When("the client creates the trainee")
    public void createTrainee() throws Exception {

        result = mockMvc.perform(post("/trainees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn();
    }

    @Then("the generated username should be {string}")
    public void generatedUsername(String username) throws Exception {

        String response = result.getResponse().getContentAsString();

        TraineeRegistrationResponse dto = objectMapper.readValue(response, TraineeRegistrationResponse.class);

        assertEquals(username, dto.getUsername());
    }

    @Then("the generated password should not be empty")
    public void passwordShouldNotBeEmpty() throws Exception {

        String response = result.getResponse().getContentAsString();

        TraineeRegistrationResponse dto = objectMapper.readValue(response, TraineeRegistrationResponse.class);

        assertNotNull(dto.getPassword());

        assertFalse(dto.getPassword().isBlank());
    }

    //////////////////////////////////////////////////////
    // UPDATE TRAINEE

    /// ///////////////////////////////////////////////////

    @Given("a valid trainee update request")
    public void validUpdateRequest() {

        updateRequest = new UpdateTraineeRequest();

        updateRequest.setUsername("Roberto.Cavalli");
        updateRequest.setFirstName("Roberto");
        updateRequest.setLastName("Cavalli");
        updateRequest.setIsActive(true);

        UpdateTraineeResponse response = new UpdateTraineeResponse();

        response.setUsername("Roberto.Cavalli");
        response.setFirstName("Roberto");
        response.setLastName("Cavalli");
        response.setAddress("Street 5th 12 30");
        response.setIsActive(true);
        response.setDateOfBirth(LocalDate.of(2000, 9, 19));
        response.setTrainers(new ArrayList<>());

        when(facade.updateTrainee(any())).thenReturn(response);
    }

    @Given("a trainee update request without username")
    public void updateWithoutUsername() {

        updateRequest = new UpdateTraineeRequest();

        updateRequest.setFirstName("Roberto");
        updateRequest.setLastName("Cavalli");
        updateRequest.setIsActive(true);
    }

    @Given("a trainee update request without first name")
    public void updateWithoutFirstName() {

        updateRequest = new UpdateTraineeRequest();

        updateRequest.setUsername("Roberto.Cavalli");
        updateRequest.setLastName("Cavalli");
        updateRequest.setIsActive(true);
    }

    @Given("a trainee {string} does not exist")
    public void traineeDoesNotExist(String username) {

        updateRequest = new UpdateTraineeRequest();

        updateRequest.setUsername(username);
        updateRequest.setFirstName("Roberto");
        updateRequest.setLastName("Cavalli");
        updateRequest.setIsActive(true);

        when(facade.updateTrainee(any())).thenThrow(new EntityNotFoundException("Trainee not found"));
    }

    @Given("a trainee update request with invalid date format")
    public void invalidDateFormat() {

        rawJson = """
                {
                  "username": "Roberto.Cavalli",
                  "firstName": "Roberto",
                  "lastName": "Cavalli",
                  "isActive": true,
                  "dateOfBirth": "19-09-2000"
                }
                """;
    }

    @When("the client updates the trainee")
    public void updateTrainee() throws Exception {

        result = mockMvc.perform(put("/trainees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRequest))).andReturn();
    }

    @When("the client updates the trainee with raw json")
    public void updateTraineeWithRawJson() throws Exception {

        result = mockMvc.perform(put("/trainees").contentType(MediaType.APPLICATION_JSON).content(rawJson)).andReturn();
    }

    @Then("the update should be successful")
    public void updateSuccessful() {
        assertEquals(200, result.getResponse().getStatus());
    }

    @Then("the trainee response status should be {int}")
    public void responseStatus(int status) {
        assertEquals(status, result.getResponse().getStatus());
    }

    //////////////////////////////////////////////////////
// GET TRAINEE

    /// ///////////////////////////////////////////////////

    @Given("a valid trainee profile request")
    public void validTraineeProfileRequest() {

        profileRequest = new TraineeProfileRequest();

        profileRequest.setUsername("Lucas.Diaz");
        profileRequest.setPassword("pass123");

        TraineeProfileResponse response = new TraineeProfileResponse();

        response.setFirstName("Lucas");
        response.setLastName("Diaz");
        response.setAddress("Street 5th 12 30");
        response.setIsActive(true);
        response.setDateOfBirth(LocalDate.of(2000, 9, 19));

        TraineeTrainersList trainer = new TraineeTrainersList();

        trainer.setUsername("Mary.Valetyn");
        trainer.setFirstName("Mary");
        trainer.setLastName("Valetyn");
        trainer.setSpecialization("Cardio");

        ArrayList<TraineeTrainersList> trainers = new ArrayList<>();

        trainers.add(trainer);

        response.setTrainers(trainers);

        when(facade.getTrainee(any())).thenReturn(response);
    }

    @Given("a trainee profile request without password")
    public void traineeProfileWithoutPassword() {

        profileRequest = new TraineeProfileRequest();

        profileRequest.setUsername("Lucas.Diaz");
    }

    @Given("a trainee profile request without username")
    public void traineeProfileWithoutUsername() {

        profileRequest = new TraineeProfileRequest();

        profileRequest.setPassword("pass123");
    }

    @Given("an empty trainee profile request")
    public void emptyTraineeProfileRequest() {

        profileRequest = new TraineeProfileRequest();
    }

    @Given("a requested trainee does not exist")
    public void requestedTraineeDoesNotExist() {

        profileRequest = new TraineeProfileRequest();

        profileRequest.setUsername("Unknown.User");
        profileRequest.setPassword("pass123");

        when(facade.getTrainee(any())).thenThrow(new EntityNotFoundException("Trainee not found"));
    }

    @Given("a trainee profile request with invalid credentials")
    public void invalidTraineeCredentials() {

        profileRequest = new TraineeProfileRequest();

        profileRequest.setUsername("Lucas.Diaz");
        profileRequest.setPassword("wrongPassword");

        when(facade.getTrainee(any())).thenThrow(new IllegalArgumentException("Invalid credentials"));
    }

    @When("the client requests the trainee profile")
    public void requestTraineeProfile() throws Exception {

        result = mockMvc.perform(get("/trainees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(profileRequest))).andReturn();
    }

    @Then("the trainee first name should be {string}")
    public void traineeFirstNameShouldBe(String firstName) throws Exception {

        String response = result.getResponse().getContentAsString();

        TraineeProfileResponse dto = objectMapper.readValue(response, TraineeProfileResponse.class);

        assertEquals(firstName, dto.getFirstName());
    }

    @Then("the trainee last name should be {string}")
    public void traineeLastNameShouldBe(String lastName) throws Exception {

        String response = result.getResponse().getContentAsString();

        TraineeProfileResponse dto = objectMapper.readValue(response, TraineeProfileResponse.class);

        assertEquals(lastName, dto.getLastName());
    }

    @Then("the trainee should be active")
    public void traineeShouldBeActive() throws Exception {

        String response = result.getResponse().getContentAsString();

        TraineeProfileResponse dto = objectMapper.readValue(response, TraineeProfileResponse.class);

        assertTrue(dto.getIsActive());
    }

    //////////////////////////////////////////////////////
// ACTIVATE / DEACTIVATE TRAINEE

    /// ///////////////////////////////////////////////////

    @Given("a valid activate or deactivate trainee request")
    public void validActivateDeactivateRequest() {

        activateRequest = new ActivateDeactivateTraineeRequest();

        activateRequest.setUsername("Jonny.Diaz");

        activateRequest.setIsActive(false);

        when(facade.activateDeactivateTrainee(any())).thenReturn(true);
    }

    @Given("an activate or deactivate request without active status")
    public void missingActiveStatus() {

        activateRequest = new ActivateDeactivateTraineeRequest();

        activateRequest.setUsername("Jonny.Diaz");
    }

    @Given("an activate or deactivate request for a non-existing trainee")
    public void activateDeactivateNonExistingTrainee() {

        activateRequest = new ActivateDeactivateTraineeRequest();

        activateRequest.setUsername("Unknown.User");

        activateRequest.setIsActive(true);

        when(facade.activateDeactivateTrainee(any())).thenThrow(new EntityNotFoundException("Trainee not found"));
    }

    @When("the client changes the trainee status")
    public void changeTraineeStatus() throws Exception {

        result = mockMvc.perform(patch("/trainees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(activateRequest))).andReturn();
    }

    //////////////////////////////////////////////////////
// DELETE TRAINEE

    /// ///////////////////////////////////////////////////

    @Given("a valid trainee deletion request")
    public void validDeleteRequest() {

        deleteRequest = new DeleteTraineeRequest();

        deleteRequest.setUsername("Felipe.Ruiz");

        when(facade.deleteTrainee(any())).thenReturn(true);
    }

    @Given("a trainee deletion request without username")
    public void deleteWithoutUsername() {

        deleteRequest = new DeleteTraineeRequest();
    }

    @Given("a deletion request for a non-existing trainee")
    public void deleteNonExistingTrainee() {

        deleteRequest = new DeleteTraineeRequest();

        deleteRequest.setUsername("Unknown.User");

        when(facade.deleteTrainee(any())).thenThrow(new EntityNotFoundException("Trainee not found"));
    }

    @When("the client deletes the trainee")
    public void clientDeletesTrainee() throws Exception {

        result = mockMvc.perform(delete("/trainees").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(deleteRequest))).andReturn();
    }

    //////////////////////////////////////////////////////
// GET TRAINEE TRAININGS LIST
    //////////////////////////////////////////////////////

    @Given("a valid trainee trainings list request")
    public void validTraineeTrainingsListRequest() {

        traineeTrainingsRequest = new TraineeTrainingsListRequest();
        traineeTrainingsRequest.setUsername("Rose.Smith");
        traineeTrainingsRequest.setFrom(LocalDate.of(2023, 1, 1));
        traineeTrainingsRequest.setTo(LocalDate.of(2023, 12, 31));
        traineeTrainingsRequest.setTrainerName("John Carter");
        traineeTrainingsRequest.setTrainingType("Cardio");

        TraineeTrainingsListResponse response =
                new TraineeTrainingsListResponse();

        response.setTrainings(new ArrayList<TrainingList>());

        when(facade.getTraineeTrainingsList(any()))
                .thenReturn(response);
    }

    @Given("a trainee trainings list request without username")
    public void traineeTrainingsWithoutUsername() {

        traineeTrainingsRequest =
                new TraineeTrainingsListRequest();

        traineeTrainingsRequest.setFrom(
                LocalDate.of(2023, 1, 1));
        traineeTrainingsRequest.setTo(
                LocalDate.of(2023, 12, 31));
    }

    @Given("a trainee trainings list request with invalid date range")
    public void traineeTrainingsInvalidDateRange() {

        traineeTrainingsRequest =
                new TraineeTrainingsListRequest();

        traineeTrainingsRequest.setUsername("Rose.Smith");
        traineeTrainingsRequest.setFrom(
                LocalDate.of(2024, 12, 31));
        traineeTrainingsRequest.setTo(
                LocalDate.of(2023, 1, 1));

        when(facade.getTraineeTrainingsList(any()))
                .thenThrow(
                        new IllegalArgumentException(
                                "Invalid date range"));
    }

    @Given("a trainee trainings request for non existing trainee")
    public void traineeTrainingsNonExisting() {

        traineeTrainingsRequest =
                new TraineeTrainingsListRequest();

        traineeTrainingsRequest.setUsername(
                "Unknown.User");

        when(facade.getTraineeTrainingsList(any()))
                .thenThrow(
                        new EntityNotFoundException(
                                "Trainee not found"));
    }

    @When("the client requests the trainee trainings list")
    public void requestTraineeTrainingsList()
            throws Exception {

        result = mockMvc.perform(
                        get("/trainees/trainingsList")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        traineeTrainingsRequest)))
                .andReturn();
    }

    //////////////////////////////////////////////////////
// UPDATE TRAINEE TRAINERS LIST
    //////////////////////////////////////////////////////

    @Given("a valid trainee trainers list update request")
    public void validTraineeTrainersListUpdateRequest() {

        updateTrainersListRequest = new UpdateTrainersListRequest();
        updateTrainersListRequest.setUsername("Julio.Domingo");

        List<UpdatedTrainersList> trainers = new ArrayList<>();
        UpdatedTrainersList trainer = new UpdatedTrainersList();
        trainer.setUsername("Carlos.Montoya");
        trainers.add(trainer);

        updateTrainersListRequest.setTrainers(trainers);

        UpdateTrainersListResponse response = new UpdateTrainersListResponse();

        List<TraineeTrainersList> updatedTrainers = new ArrayList<>();
        TraineeTrainersList updated = new TraineeTrainersList();
        updated.setUsername("Carlos.Montoya");
        updated.setFirstName("Carlos");
        updated.setLastName("Montoya");
        updated.setSpecialization("Fitness");

        updatedTrainers.add(updated);

        response.setUpdatedTrainers(updatedTrainers);

        when(facade.UpdateTraineeTrainersList(any()))
                .thenReturn(response);
    }

    @Given("a trainee trainers list update request without username")
    public void traineeTrainersListWithoutUsername() {

        updateTrainersListRequest = new UpdateTrainersListRequest();
        updateTrainersListRequest.setTrainers(new ArrayList<>());
    }

    @Given("a trainee trainers list request for a non existing trainee")
    public void traineeTrainersListNonExistingTrainee() {

        updateTrainersListRequest = new UpdateTrainersListRequest();
        updateTrainersListRequest.setUsername("Unknown.User");

        UpdatedTrainersList trainer = new UpdatedTrainersList();
        trainer.setUsername("Carlos.Montoya");

        updateTrainersListRequest.setTrainers(
                List.of(trainer));

        when(facade.UpdateTraineeTrainersList(any()))
                .thenThrow(
                        new EntityNotFoundException(
                                "User not found"));
    }

    @Given("a trainee trainers list request with a non existing trainer")
    public void traineeTrainersListNonExistingTrainer() {

        updateTrainersListRequest = new UpdateTrainersListRequest();
        updateTrainersListRequest.setUsername("Julio.Domingo");

        UpdatedTrainersList trainer = new UpdatedTrainersList();
        trainer.setUsername("Non.Existing.Trainer");

        updateTrainersListRequest.setTrainers(
                List.of(trainer));

        when(facade.UpdateTraineeTrainersList(any()))
                .thenThrow(
                        new EntityNotFoundException(
                                "Trainer not found"));
    }

    @When("the client updates the trainee trainers list")
    public void updateTraineeTrainersList()
            throws Exception {

        result = mockMvc.perform(
                        put("/trainees/trainersList")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        updateTrainersListRequest)))
                .andReturn();
    }
}