package com.gym.system.workload.repository;

import com.gym.system.workload.model.TrainerTrainingSummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TrainerTrainingSummaryRepository extends MongoRepository<TrainerTrainingSummary, String> {
    Optional<TrainerTrainingSummary> findByUsername(String username);
}