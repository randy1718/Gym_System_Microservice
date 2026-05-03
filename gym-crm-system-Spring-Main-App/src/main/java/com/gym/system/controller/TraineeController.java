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
    name = "Trainees",
    description = "Operations related to trainee management (create, update, retrieve, delete, and status (isActive) management)"
)
@RestController
@RequestMapping("/trainees")
public class TraineeController {

    private final GymServices facade;

    public TraineeController(GymServices facade) {
        this.facade = facade;
    }

    @Operation(description = "Register a new Trainee by providing their information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainee is created correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PostMapping
    public ResponseEntity<TraineeRegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationRequest request) {

        TraineeRegistrationResponse response = facade.createTrainee(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Update the information of a trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainee is updated correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PutMapping
    public ResponseEntity<UpdateTraineeResponse> updateTrainee(
            @Valid @RequestBody UpdateTraineeRequest request) {

        UpdateTraineeResponse response = facade.updateTrainee(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Update trainee's trainers list by providing the trainer username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainee's trainers list is updated correctly."),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PutMapping("/trainersList")
    public ResponseEntity<UpdateTrainersListResponse> UpdateTraineeTrainersList(
            @Valid @RequestBody UpdateTrainersListRequest request) {

        UpdateTrainersListResponse response = facade.UpdateTraineeTrainersList(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get the information of a trainee by providing their username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The information of the trainee is retrieved correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping
    public ResponseEntity<TraineeProfileResponse> getTrainee(
            @Valid @RequestBody TraineeProfileRequest request) {

        TraineeProfileResponse response = facade.getTrainee(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Get trainee's trainings by providing their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainee's trainings are retrieved correctly."),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping("/trainingsList")
    public ResponseEntity<TraineeTrainingsListResponse> getTraineeTrainingsList(
            @Valid @RequestBody TraineeTrainingsListRequest request) {

        TraineeTrainingsListResponse response = facade.getTraineeTrainingsList(request);

        return ResponseEntity.ok(response);
    }

    @Operation(description = "Delete a trainee by providing the respective user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainee is deleted correctly."),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteTrainee(
            @Valid @RequestBody DeleteTraineeRequest request) {

        Boolean authenticated = facade.deleteTrainee(request);

        if(authenticated){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(description = "Change the value of isActive variable of a trainee to true or false")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The value of Trainee's isActive is changed correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PatchMapping
    public ResponseEntity<Void> activateDeactivateTrainee(
            @Valid @RequestBody ActivateDeactivateTraineeRequest request) {

        Boolean authenticated = facade.activateDeactivateTrainee(request);

        if(authenticated){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
