package com.gym.system.workload.controller;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "Add hours to Trainer's Workload", description = "Endpoint to calculate the workload of an specific trainer.")
@RestController
@RequestMapping("/workload")
public class CalculateTrainerWorkloadController {

    private final TrainerService trainerService;

    public CalculateTrainerWorkloadController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Operation(description = "Calculate trainer's workload depending or a new or deleted training")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The trainer's workload is updated correctly"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @PostMapping
    public ResponseEntity<Void> calculateWorkload(
            @Valid @RequestBody CalculateTrainerWorkloadRequest request) {

        trainerService.calculateWorkload(request);
        return ResponseEntity.ok().build();
    }

}
