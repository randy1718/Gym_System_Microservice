package com.gym.system.workload;

import static org.junit.jupiter.api.Assertions.*;
import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.service.TrainerService;
import com.gym.system.workload.service.strategy.AddWorkloadStrategy;
import com.gym.system.workload.service.strategy.DeleteWorkloadStrategy;
import com.gym.system.workload.service.strategy.WorkloadStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

class GymSystemWorloadApplicationTests {

	private TrainerService trainerService;

    @BeforeEach
	void setUp() {
		WorkloadStrategyFactory strategyFactory =
				new WorkloadStrategyFactory(
						new AddWorkloadStrategy(),
						new DeleteWorkloadStrategy()
				);

		trainerService = new TrainerService(strategyFactory);
	}

	@Test
	void shouldAddWorkloadForNewTrainer() {

		CalculateTrainerWorkloadRequest request = new CalculateTrainerWorkloadRequest();
		request.setTrainerUsername("john");
		request.setFirstName("John");
		request.setLastName("Doe");
		request.setIsActive(true);
		request.setTrainingDuration(60);
		request.setTrainingDate(LocalDate.of(2026, 1, 10));
		request.setActionType("ADD");

		trainerService.calculateWorkload(request);

		// assert
		assertTrue(trainerService.getTrainers().containsKey("john"));
		assertEquals(60,
				trainerService.getTrainers()
						.get("john")
						.getYears()
						.get(2026)
						.get(1));
	}

	@Test
	void shouldAccumulateWorkload() {

		CalculateTrainerWorkloadRequest request = new CalculateTrainerWorkloadRequest();
		request.setTrainerUsername("john");
		request.setTrainingDuration(60);
		request.setTrainingDate(LocalDate.of(2026, 1, 10));
		request.setActionType("ADD");

		trainerService.calculateWorkload(request);
		trainerService.calculateWorkload(request);

		int result = trainerService.getTrainers()
				.get("john")
				.getYears()
				.get(2026)
				.get(1);

		assertEquals(120, result);
	}

	@Test
	void shouldDeleteWorkload() {

		CalculateTrainerWorkloadRequest request = new CalculateTrainerWorkloadRequest();
		request.setTrainerUsername("john");
		request.setTrainingDuration(60);
		request.setTrainingDate(LocalDate.of(2026, 1, 10));
		request.setActionType("ADD");

		trainerService.calculateWorkload(request);

		request.setActionType("DELETE");
		trainerService.calculateWorkload(request);

		Map<Integer, Integer> months = trainerService.getTrainers()
				.get("john")
				.getYears()
				.get(2026);

		assertFalse(months.containsKey(1));
	}

}
