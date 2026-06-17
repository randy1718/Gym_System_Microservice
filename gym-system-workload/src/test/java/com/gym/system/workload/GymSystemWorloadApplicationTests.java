package com.gym.system.workload;

import static org.junit.jupiter.api.Assertions.*;
import com.gym.system.shared.dto.CalculateTrainerWorkloadRequest;
import com.gym.system.workload.model.TrainerTrainingSummary;
import com.gym.system.workload.repository.TrainerTrainingSummaryRepository;
import com.gym.system.workload.service.TrainerService;
import com.gym.system.workload.service.strategy.AddWorkloadStrategy;
import com.gym.system.workload.service.strategy.DeleteWorkloadStrategy;
import com.gym.system.workload.service.strategy.WorkloadStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Optional;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class GymSystemWorloadApplicationTests {

	@InjectMocks
	private TrainerService trainerService;

	@Mock
	private TrainerTrainingSummaryRepository repository;

    @BeforeEach
	void setUp() {
		repository = mock(TrainerTrainingSummaryRepository.class);

		when(repository.save(any(TrainerTrainingSummary.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		WorkloadStrategyFactory strategyFactory =
				new WorkloadStrategyFactory(
						new AddWorkloadStrategy(),
						new DeleteWorkloadStrategy()
				);

		trainerService = new TrainerService(strategyFactory, repository);
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

		when(repository.findByUsername("john"))
				.thenReturn(Optional.empty());

		trainerService.calculateWorkload(request);

		// assert
		verify(repository).save(
				argThat(trainer ->
						trainer.getYears()
								.get(2026)
								.get(1) == 60
				)
		);
	}

	@Test
	void shouldAccumulateWorkload() {

		TrainerTrainingSummary trainer =
				new TrainerTrainingSummary();

		trainer.setUsername("john");
		trainer.setYears(new HashMap<>());

		trainer.getYears()
				.computeIfAbsent(2026, y -> new HashMap<>())
				.put(1, 60);

		when(repository.findByUsername("john"))
				.thenReturn(Optional.of(trainer));

		CalculateTrainerWorkloadRequest request = new CalculateTrainerWorkloadRequest();
		request.setTrainerUsername("john");
		request.setTrainingDuration(60);
		request.setTrainingDate(LocalDate.of(2026, 1, 10));
		request.setActionType("ADD");

		trainerService.calculateWorkload(request);

		assertEquals(
				120,
				trainer.getYears()
						.get(2026)
						.get(1)
		);

		verify(repository).save(trainer);
	}

	@Test
	void shouldDeleteWorkload() {

		TrainerTrainingSummary trainer =
				new TrainerTrainingSummary();

		trainer.setUsername("john");
		trainer.setYears(new HashMap<>());

		trainer.getYears()
				.computeIfAbsent(2026, y -> new HashMap<>())
				.put(1, 60);

		when(repository.findByUsername("john"))
				.thenReturn(Optional.of(trainer));


		CalculateTrainerWorkloadRequest request = new CalculateTrainerWorkloadRequest();
		request.setTrainerUsername("john");
		request.setTrainingDuration(60);
		request.setTrainingDate(LocalDate.of(2026, 1, 10));
		request.setActionType("DELETE");

		trainerService.calculateWorkload(request);

		assertFalse(
				trainer.getYears()
						.get(2026)
						.containsKey(1)
		);

		verify(repository).save(trainer);
	}

}
