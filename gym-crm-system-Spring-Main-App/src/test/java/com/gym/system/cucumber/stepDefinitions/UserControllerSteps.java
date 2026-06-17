package com.gym.system.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.controller.UserController;
import com.gym.system.dto.ChangeLoginRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserControllerSteps {

    private MockMvc mockMvc;
    private GymServices facade;
    private ObjectMapper objectMapper;
    private ChangeLoginRequest request;
    private MvcResult result;

    @Before
    public void setup() {

        facade = Mockito.mock(GymServices.class);

        UserController controller =
                new UserController(facade);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(
                        new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        objectMapper = new ObjectMapper();

        request = new ChangeLoginRequest();
    }

    @Given("a user {string} exists")
    public void userExists(String username) {

        request.setUsername(username);
    }

    @Given("a user {string} does not exist")
    public void userDoesNotExist(String username) {

        request.setUsername(username);
        request.setOldPassword("OldPass123");
        request.setNewPassword("NewPass123!");

        when(facade.changeLogin(any()))
                .thenThrow(
                        new EntityNotFoundException(
                                "User not found"));
    }

    @Given("the old password is correct")
    public void oldPasswordCorrect() {

        request.setOldPassword("Fagobian18");

        when(facade.changeLogin(any()))
                .thenReturn(true);
    }

    @Given("the old password is invalid")
    public void oldPasswordInvalid() {

        request.setOldPassword("WrongPassword");

        when(facade.changeLogin(any()))
                .thenThrow(
                        new IllegalArgumentException(
                                "Invalid credentials"));
    }

    @When("the user changes the password to {string}")
    public void changePassword(String newPassword)
            throws Exception {

        request.setNewPassword("NewPass123!");

        result = mockMvc.perform(
                        put("/users/password")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andReturn();
    }

    @When("the old password is omitted")
    public void oldPasswordOmitted()
            throws Exception {

        request.setNewPassword("NewPass123!");

        result = mockMvc.perform(
                        put("/users/password")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andReturn();
    }

    @When("the new password is omitted")
    public void newPasswordOmitted()
            throws Exception {

        request.setOldPassword("OldPass123");

        result = mockMvc.perform(
                        put("/users/password")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andReturn();
    }

    @When("the user requests a password change")
    public void requestPasswordChange()
            throws Exception {

        request.setNewPassword("NewPass123!");

        result = mockMvc.perform(
                        put("/users/password")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andReturn();
    }

    @Then("the user request should be successful")
    public void requestSuccessful() {
        assertEquals(
                200,
                result.getResponse().getStatus());
    }

    @Then("the user response status should be {int}")
    public void responseStatus(int status) {
        assertEquals(
                status,
                result.getResponse().getStatus());
    }
}