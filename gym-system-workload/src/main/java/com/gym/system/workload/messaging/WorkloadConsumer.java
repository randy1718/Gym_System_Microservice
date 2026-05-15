package com.gym.system.workload.messaging;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.service.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class WorkloadConsumer {

    private final TrainerService trainerService;
    private static final Logger logger = LoggerFactory.getLogger(WorkloadConsumer.class);

    public WorkloadConsumer(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @JmsListener(destination = "workload.queue")
    public void receiveWorkload(
            CalculateTrainerWorkloadRequest request) {
        System.out.println("MESSAGE RECEIVED");
        logger.info(
                "MESSAGE RECEIVED!!!!!!!");
        trainerService.calculateWorkload(request);
    }
}