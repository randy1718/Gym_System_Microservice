package com.gym.system.exception;

public record ErrorResponse(
        String code,
        String message
) {}