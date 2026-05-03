package com.gym.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.system.dto.TrainingTypesResponse;
import com.gym.system.service.GymServices;

@Tag(name = "Get training types", description = "Endpoint to get all training types")
@RestController
@RequestMapping("/trainingTypes")
public class TrainingTypesController {
    private final GymServices facade;

    public TrainingTypesController(GymServices facade) {
        this.facade = facade;
    }

    @Operation(description = "Get all training types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types are retrieved correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping
    public ResponseEntity<TrainingTypesResponse> getTrainingTypes() {

        TrainingTypesResponse response = facade.getTrainingTypes();

        return ResponseEntity.ok(response);
    }
}    