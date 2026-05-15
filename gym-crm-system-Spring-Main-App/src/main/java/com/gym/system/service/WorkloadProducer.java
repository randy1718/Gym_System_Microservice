package com.gym.system.service;

import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkloadProducer {

    private final JmsTemplate jmsTemplate;

    public WorkloadProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendWorkload(CalculateTrainerWorkloadRequest request) {

        jmsTemplate.convertAndSend(
                "workload.queue",
                request
        );
    }
}