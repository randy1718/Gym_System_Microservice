package com.gym.system.controller;

import com.gym.system.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gym.system.service.GymServices;

import jakarta.validation.Valid;

@Tag(
    name = "Trainers",
    description = "Operations related to trainer management (create, update, retrieve, and status (isActive) management)"
)
@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final GymServices facade;

    public TrainerController(GymServices facade) {
        this.facade = facade;
    }

    @Operation(description = "Register a new Trainer by providing their information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainer is created correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PostMapping
    public ResponseEntity<TrainerRegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationRequest request) {

        TrainerRegistrationResponse response = facade.createTrainer(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Update the information of a trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainer is updated correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PutMapping
    public ResponseEntity<UpdateTrainerResponse> updateTrainer(
            @Valid @RequestBody UpdateTrainerRequest request) {

        UpdateTrainerResponse response = facade.updateTrainer(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get the information of a trainer by providing their username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The information of the trainer is retrieved correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping
    public ResponseEntity<TrainerProfileResponse> getTrainer(
            @Valid @RequestBody TrainerProfileRequest request) {

        TrainerProfileResponse response = facade.getTrainer(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get trainer's trainings by providing their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainer's trainings are retrieved correctly."),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping("/trainingsList")
    public ResponseEntity<TrainerTrainingsListResponse> getTrainerTrainingsList(
            @Valid @RequestBody TrainerTrainingsListRequest request) {

        TrainerTrainingsListResponse response = facade.getTrainerTrainingsList(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get all unassigned trainers who don´t have trainings with the specific trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unassigned trainers are retrieved correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping("/unassigned")
    public ResponseEntity<UnassignedTrainersResponse> getUnassignedTrainers(
            @Valid @RequestBody UnassignedTrainersRequest request) {

        UnassignedTrainersResponse response = facade.getUnassignedTrainers(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Change the value of isActive variable of a trainer to true or false")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The value of Trainer's isActive is changed correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PatchMapping
    public ResponseEntity<Void> activateDeactivateTrainer(
            @Valid @RequestBody ActivateDeactivateTrainerRequest request) {

        Boolean authenticated = facade.activateDeactivateTrainer(request);

        if(authenticated){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
