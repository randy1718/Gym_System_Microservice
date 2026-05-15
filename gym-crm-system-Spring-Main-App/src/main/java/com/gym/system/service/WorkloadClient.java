package com.gym.system.service;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WorkloadClient {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(WorkloadClient.class);

    public WorkloadClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackWorkload")
    public void sendWorkload(CalculateTrainerWorkloadRequest request, String token) {

        webClient.post()
                .uri("http://gym-system-workload/workload")
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void fallbackWorkload(CalculateTrainerWorkloadRequest request, String token, Throwable ex) {
        logger.error("Workload service failed, fallback triggered");
    }

}
