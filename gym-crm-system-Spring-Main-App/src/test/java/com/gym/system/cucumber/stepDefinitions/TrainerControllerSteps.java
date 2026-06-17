package com.gym.system.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.TrainerController;
import com.gym.system.dto.TrainerRegistrationRequest;
import com.gym.system.dto.TrainerRegistrationResponse;
import com.gym.system.dto.UpdateTrainerRequest;
import com.gym.system.dto.UpdateTrainerResponse;
import com.gym.system.dto.TrainerProfileRequest;
import com.gym.system.dto.TrainerProfileResponse;
import com.gym.system.dto.TrainerTraineesList;
import com.gym.system.dto.ActivateDeactivateTrainerRequest;
import com.gym.system.dto.TrainerTrainingsListRequest;
import com.gym.system.dto.TrainerTrainingsListResponse;
import com.gym.system.dto.TrainerTrainingList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TrainerControllerSteps {

    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;

    private TrainerRegistrationRequest registrationRequest;
    private UpdateTrainerRequest updateRequest;
    private TrainerProfileRequest profileRequest;
    private ActivateDeactivateTrainerRequest activateRequest;
    private TrainerTrainingsListRequest trainingsRequest;
    private MvcResult result;

    @Before
    public void setup() {

        facade = Mockito.mock(GymServices.class);

        TrainerController controller = new TrainerController(facade);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).setValidator(validator).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        registrationRequest = new TrainerRegistrationRequest();
    }

    //////////////////////////////////////////////////////
    // CREATE TRAINER

    /// ///////////////////////////////////////////////////

    @Given("a valid trainer registration request")
    public void validTrainerRegistrationRequest() {

        registrationRequest.setFirstName("Mario");

        registrationRequest.setLastName("Hernandez");

        registrationRequest.setSpecializationName("Cardio");

        TrainerRegistrationResponse response = new TrainerRegistrationResponse();

        response.setUsername("Mario.Hernandez");

        response.setPassword("1234567qasdAS");

        when(facade.createTrainer(any())).thenReturn(response);
    }

    @Given("a trainer request without first name")
    public void trainerWithoutFirstName() {

        registrationRequest.setLastName("Hernandez");

        registrationRequest.setSpecializationName("Cardio");
    }

    @Given("a trainer request without specialization")
    public void trainerWithoutSpecialization() {

        registrationRequest.setFirstName("Mario");

        registrationRequest.setLastName("Hernandez");
    }

    @Given("a trainer request with an invalid specialization")
    public void invalidSpecialization() {

        registrationRequest.setFirstName("Mario");

        registrationRequest.setLastName("Hernandez");

        registrationRequest.setSpecializationName("UnknownType");

        when(facade.createTrainer(any())).thenThrow(new IllegalArgumentException("Invalid specialization"));
    }

    @When("the client creates the trainer")
    public void clientCreatesTrainer() throws Exception {

        result = mockMvc.perform(post("/trainers").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(registrationRequest))).andReturn();
    }

    @Then("the generated trainer username should be {string}")
    public void generatedTrainerUsername(String username) throws Exception {

        String response = result.getResponse().getContentAsString();

        TrainerRegistrationResponse dto = objectMapper.readValue(response, TrainerRegistrationResponse.class);

        assertEquals(username, dto.getUsername());
    }

    @Then("the generated trainer password should not be empty")
    public void generatedTrainerPassword() throws Exception {

        String response = result.getResponse().getContentAsString();

        TrainerRegistrationResponse dto = objectMapper.readValue(response, TrainerRegistrationResponse.class);

        assertNotNull(dto.getPassword());

        assertFalse(dto.getPassword().isBlank());
    }

    @Then("the trainer response status should be {int}")
    public void trainerResponseStatus(int status) {

        assertEquals(status, result.getResponse().getStatus());
    }

    //////////////////////////////////////////////////////
// UPDATE TRAINER

    /// ///////////////////////////////////////////////////

    @Given("a valid trainer update request")
    public void validTrainerUpdateRequest() {

        updateRequest = new UpdateTrainerRequest();
        updateRequest.setUsername("Alfredo.Cavalli");
        updateRequest.setFirstName("Alfredo");
        updateRequest.setLastName("Cavalli");
        updateRequest.setSpecialization("Cardio");
        updateRequest.setIsActive(true);

        UpdateTrainerResponse response = new UpdateTrainerResponse();
        response.setUsername("Alfredo.Cavalli");
        response.setFirstName("Alfredo");
        response.setLastName("Cavalli");
        response.setSpecialization("Cardio");
        response.setIsActive(true);
        response.setTrainees(new ArrayList<>());

        when(facade.updateTrainer(any())).thenReturn(response);
    }

    @Given("a trainer update request without username")
    public void trainerUpdateWithoutUsername() {

        updateRequest = new UpdateTrainerRequest();

        updateRequest.setFirstName("Alfredo");

        updateRequest.setLastName("Cavalli");

        updateRequest.setSpecialization("Cardio");

        updateRequest.setIsActive(true);
    }

    @Given("a trainer {string} does not exist")
    public void trainerDoesNotExist(String username) {

        updateRequest = new UpdateTrainerRequest();

        updateRequest.setUsername(username);

        updateRequest.setFirstName("Alfredo");

        updateRequest.setLastName("Cavalli");

        updateRequest.setSpecialization("Cardio");

        updateRequest.setIsActive(true);

        when(facade.updateTrainer(any())).thenThrow(new EntityNotFoundException("Trainer not found"));
    }

    @Given("a trainer update request with invalid specialization")
    public void invalidTrainerSpecialization() {

        updateRequest = new UpdateTrainerRequest();

        updateRequest.setUsername("Alfredo.Cavalli");

        updateRequest.setFirstName("Alfredo");

        updateRequest.setLastName("Cavalli");

        updateRequest.setSpecialization("AlienTraining");

        updateRequest.setIsActive(true);

        when(facade.updateTrainer(any())).thenThrow(new EntityNotFoundException("Training type not found"));
    }

    @When("the client updates the trainer")
    public void clientUpdatesTrainer() throws Exception {

        result = mockMvc.perform(put("/trainers").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateRequest))).andReturn();
    }

    @Then("the updated trainer username should be {string}")
    public void updatedTrainerUsername(String username) throws Exception {

        String response = result.getResponse().getContentAsString();

        UpdateTrainerResponse dto = objectMapper.readValue(response, UpdateTrainerResponse.class);

        assertEquals(username, dto.getUsername());
    }

    //////////////////////////////////////////////////////
// GET TRAINER
    /// ///////////////////////////////////////////////////

    @Given("a valid trainer profile request")
    public void validTrainerProfileRequest() {

        profileRequest = new TrainerProfileRequest();
        profileRequest.setUsername("Mary.Valetyn");
        profileRequest.setPassword("passer1234232");

        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstName("Mary");
        response.setLastName("Valetyn");
        response.setIsActive(true);
        response.setSpecialization("Cardio");

        TrainerTraineesList trainee = new TrainerTraineesList();
        trainee.setFirstName("Gustavo Pereira");

        List<TrainerTraineesList> trainees = new ArrayList<>();
        trainees.add(trainee);

        response.setTrainees(trainees);

        when(facade.getTrainer(any())).thenReturn(response);
    }

    @Given("a trainer profile request without password")
    public void trainerProfileWithoutPassword() {

        profileRequest = new TrainerProfileRequest();
        profileRequest.setUsername("Mary.Valetyn");
    }

    @Given("trainer profile for {string} does not exist")
    public void trainerProfileDoesNotExist(String username) {

        profileRequest = new TrainerProfileRequest();
        profileRequest.setUsername(username);
        profileRequest.setPassword("pass123");

        when(facade.getTrainer(any())).thenThrow(new EntityNotFoundException("Trainer not found"));
    }

    @Given("a trainer profile request with invalid credentials")
    public void invalidTrainerCredentials() {

        profileRequest = new TrainerProfileRequest();
        profileRequest.setUsername("Mary.Valetyn");
        profileRequest.setPassword("wrongPassword");

        when(facade.getTrainer(any())).thenThrow(new IllegalArgumentException("Invalid credentials"));
    }

    @When("the client gets the trainer")
    public void clientGetsTrainer() throws Exception {
        result = mockMvc.perform(get("/trainers").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(profileRequest))).andReturn();
    }

    @Then("the trainer first name should be {string}")
    public void trainerFirstNameShouldBe(String firstName) throws Exception {

        String response = result.getResponse().getContentAsString();
        TrainerProfileResponse dto = objectMapper.readValue(response, TrainerProfileResponse.class);
        assertEquals(firstName, dto.getFirstName());
    }

    @Then("the trainer last name should be {string}")
    public void trainerLastNameShouldBe(String lastName) throws Exception {

        String response = result.getResponse().getContentAsString();
        TrainerProfileResponse dto = objectMapper.readValue(response, TrainerProfileResponse.class);
        assertEquals(lastName, dto.getLastName());
    }

    @Then("the trainer specialization should be {string}")
    public void trainerSpecializationShouldBe(String specialization) throws Exception {

        String response = result.getResponse().getContentAsString();
        TrainerProfileResponse dto = objectMapper.readValue(response, TrainerProfileResponse.class);
        assertEquals(specialization, dto.getSpecialization());
    }

    //////////////////////////////////////////////////////
// ACTIVATE / DEACTIVATE TRAINER
    //////////////////////////////////////////////////////

    @Given("a valid trainer activation request")
    public void validTrainerActivationRequest() {
        activateRequest = new ActivateDeactivateTrainerRequest();
        activateRequest.setUsername("Luisa.Vallez");
        activateRequest.setIsActive(true);

        when(facade.activateDeactivateTrainer(any()))
                .thenReturn(true);
    }

    @Given("a trainer activation request without isActive")
    public void trainerActivationWithoutIsActive() {
        activateRequest = new ActivateDeactivateTrainerRequest();
        activateRequest.setUsername("Jonny.Diaz");
    }

    @Given("trainer activation for {string} does not exist")
    public void trainerActivationDoesNotExist(String username) {
        activateRequest = new ActivateDeactivateTrainerRequest();
        activateRequest.setUsername(username);
        activateRequest.setIsActive(true);

        when(facade.activateDeactivateTrainer(any()))
                .thenThrow(new EntityNotFoundException("Trainer not found"));
    }

    @When("the client activates or deactivates the trainer")
    public void clientActivatesOrDeactivatesTrainer() throws Exception {
        result = mockMvc.perform(
                patch("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activateRequest))
        ).andReturn();
    }

    //////////////////////////////////////////////////////
// GET TRAINER TRAININGS
    //////////////////////////////////////////////////////

    @Given("a valid trainer trainings request")
    public void validTrainerTrainingsRequest() {
        trainingsRequest = new TrainerTrainingsListRequest();
        trainingsRequest.setUsername("Camilo.Diaz");
        trainingsRequest.setFrom(LocalDate.of(2024, 1, 1));
        trainingsRequest.setTo(LocalDate.of(2025, 12, 31));
        trainingsRequest.setTraineeName("Veronica Leon");

        TrainerTrainingList training = new TrainerTrainingList();
        training.setTraineeName("Veronica Leon");
        training.setTrainingDate("2024-05-20");
        training.setDuration(60);
        training.setTrainingName("Morning Strength Session - Veronica");
        training.setTrainingType("Strength");

        List<TrainerTrainingList> trainings = new ArrayList<>();
        trainings.add(training);

        TrainerTrainingsListResponse response = new TrainerTrainingsListResponse();
        response.setTrainings(trainings);

        when(facade.getTrainerTrainingsList(any()))
                .thenReturn(response);
    }

    @Given("a trainer trainings request without username")
    public void trainerTrainingsWithoutUsername() {
        trainingsRequest = new TrainerTrainingsListRequest();
        trainingsRequest.setFrom(LocalDate.of(2024, 1, 1));
        trainingsRequest.setTo(LocalDate.of(2025, 12, 31));
    }

    @Given("a trainer trainings request with invalid date range")
    public void invalidTrainerDateRange() {
        trainingsRequest = new TrainerTrainingsListRequest();
        trainingsRequest.setUsername("Camilo.Diaz");
        trainingsRequest.setFrom(LocalDate.of(2025, 12, 31));
        trainingsRequest.setTo(LocalDate.of(2024, 1, 1));

        when(facade.getTrainerTrainingsList(any()))
                .thenThrow(new IllegalArgumentException("Invalid date range"));
    }

    @Given("trainer trainings for {string} do not exist")
    public void trainerTrainingsDoNotExist(String username) {
        trainingsRequest = new TrainerTrainingsListRequest();
        trainingsRequest.setUsername(username);

        when(facade.getTrainerTrainingsList(any()))
                .thenThrow(new EntityNotFoundException("Trainer not found"));
    }

    @When("the client gets the trainer trainings")
    public void clientGetsTrainerTrainings() throws Exception {
        result = mockMvc.perform(
                get("/trainers/trainingsList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingsRequest))
        ).andReturn();
    }

    @Then("the trainer should have {int} trainings")
    public void trainerShouldHaveTrainings(int expected) throws Exception {
        String response = result.getResponse().getContentAsString();

        TrainerTrainingsListResponse dto =
                objectMapper.readValue(
                        response,
                        TrainerTrainingsListResponse.class
                );

        assertEquals(expected, dto.getTrainings().size());
    }

    @Then("the first trainee name should be {string}")
    public void firstTraineeNameShouldBe(String expected) throws Exception {
        String response = result.getResponse().getContentAsString();

        TrainerTrainingsListResponse dto =
                objectMapper.readValue(
                        response,
                        TrainerTrainingsListResponse.class
                );

        assertEquals(
                expected,
                dto.getTrainings().get(0).getTraineeName()
        );
    }
}