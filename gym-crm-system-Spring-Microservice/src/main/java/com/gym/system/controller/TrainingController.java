package com.gym.system.controller;

import com.gym.system.dto.DeleteTrainingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gym.system.dto.AddTrainingRequest;
import com.gym.system.service.GymServices;

import jakarta.validation.Valid;

@Tag(
    name = "Trainings",
    description = "Operations related to training management (create, update, retrieve, delete and status (isActive) management)"
)
@RestController
@RequestMapping("/trainings")
public class TrainingController {
    private final GymServices facade;

    public TrainingController(GymServices facade) {
        this.facade = facade;
    }

    @Operation(description = "Add a training with the data provided")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The training is created correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(
            @Valid @RequestBody AddTrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Boolean authenticated = facade.addTraining(request, token);

        if(authenticated){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(description = "Delete a training by providing the respective trainer and trainee username and the training date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The training is deleted correctly."),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteTraining(
            @Valid @RequestBody DeleteTrainingRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        Boolean deleted = facade.deleteTraining(request, token);

        if(deleted){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
