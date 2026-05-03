package com.gym.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.system.dto.LoginRequest;
import com.gym.system.service.GymServices;

import jakarta.validation.Valid;

@Tag(name = "Login as a trainer or trainee", description = "Endpoint to login as a trainee or trainer")
@RestController
@RequestMapping("/login")
public class LoginController {
    private final GymServices facade;

    public LoginController(GymServices facade) {
        this.facade = facade;
    }

    @Operation(description = "Login by providing trainee's or trainer's credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee's or Trainer's credentials are correct."),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Invalid server error")
    })
    @GetMapping
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request) {

        Boolean authenticated = facade.login(request);

        if(authenticated){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}    