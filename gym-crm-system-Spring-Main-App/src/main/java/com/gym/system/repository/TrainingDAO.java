package com.gym.system.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.gym.system.monitoring.metrics.CountTrainingsMetric;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gym.system.dto.TraineeTrainingsListRequest;
import com.gym.system.dto.TrainerTrainingsListRequest;
import com.gym.system.model.Trainer;
import com.gym.system.model.Training;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class TrainingDAO {

    @PersistenceContext
    private EntityManager em;

    private final CountTrainingsMetric countTrainingsMetric;

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);

    public TrainingDAO(CountTrainingsMetric countTrainingsMetric) {
        this.countTrainingsMetric = countTrainingsMetric;
    }

    public void save(Training training) {
        logger.debug("Saving training in Database {}", training.getTrainingName());
        em.persist(training);
        em.flush();
        countTrainingsMetric.increment();
    }

    public void delete(Long id) {
        logger.debug("Deleting training {}", id);
        Training training = em.find(Training.class, id);
        if (training != null) {
            em.remove(training);
        }
    }

    public Optional<Training> findById(String id) {
        logger.debug("Finding training {}", id);
        return Optional.ofNullable(em.find(Training.class, id));
    }

    public List<Training> findTrainingsByTraineeUsername(String username, String fromDate, String toDate,
            String trainerName, String trainingType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedFromDate = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime formattedToDate = LocalDateTime.parse(toDate, formatter);
        return em.createQuery(
                "SELECT t FROM Training t WHERE t.trainee.username = :username AND t.trainingDate BETWEEN :fromDate AND :toDate AND t.trainer.firstName = :trainerName AND t.trainingType.name = :trainingType",
                Training.class).setParameter("username", username)
                .setParameter("fromDate", formattedFromDate)
                .setParameter("toDate", formattedToDate)
                .setParameter("trainerName", trainerName)
                .setParameter("trainingType", trainingType)
                .getResultList();
    }

    public List<Training> findTrainingsByTrainerUsername(String username, String fromDate, String toDate,
            String traineeName, String trainingType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedFromDate = LocalDateTime.parse(fromDate, formatter);
        LocalDateTime formattedToDate = LocalDateTime.parse(toDate, formatter);
        return em.createQuery(
                "SELECT t FROM Training t WHERE t.trainer.username = :username AND t.trainingDate BETWEEN :fromDate AND :toDate AND t.trainee.firstName = :traineeName AND t.trainingType.name = :trainingType",
                Training.class).setParameter("username", username)
                .setParameter("fromDate", formattedFromDate)
                .setParameter("toDate", formattedToDate)
                .setParameter("traineeName", traineeName)
                .setParameter("trainingType", trainingType)
                .getResultList();
    }

    public List<Training> findTraineeTrainings(TraineeTrainingsListRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedFromDate = request.getFrom() != null ? LocalDateTime.parse(request.getFrom().toString(), formatter)
                : null;
        LocalDateTime formattedToDate = request.getTo() != null ? LocalDateTime.parse(request.getTo().toString(), formatter)
                : null;
        StringBuilder jpql = new StringBuilder(
                "SELECT t FROM Training t WHERE t.trainee.username = :username");

        if (request.getFrom() != null) {
            jpql.append(" AND t.trainingDate >= :fromDate");
        }

        if (request.getTo() != null) {
            jpql.append(" AND t.trainingDate <= :toDate");
        }

        if (request.getTrainerName() != null) {
            jpql.append(" AND t.trainer.username = :trainerName");
        }

        if (request.getTrainingType() != null) {
            jpql.append(" AND t.trainingType.name = :trainingType");
        }

        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);

        query.setParameter("username", request.getUsername());

        if (request.getFrom() != null) {
            query.setParameter("fromDate", formattedFromDate);
        }

        if (request.getTo() != null) {
            query.setParameter("toDate", formattedToDate);
        }

        if (request.getTrainerName() != null) {
            query.setParameter("trainerName", request.getTrainerName());
        }

        if (request.getTrainingType() != null) {
            query.setParameter("trainingType", request.getTrainingType());
        }

        return query.getResultList();
    }

    public List<Training> findTrainerTrainings(TrainerTrainingsListRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedFromDate = request.getFrom() != null ? LocalDateTime.parse(request.getFrom().toString(), formatter)
                : null;
        LocalDateTime formattedToDate = request.getTo() != null ? LocalDateTime.parse(request.getTo().toString(), formatter)
                : null;
        StringBuilder jpql = new StringBuilder(
                "SELECT t FROM Training t WHERE t.trainer.username = :username");

        if (request.getFrom() != null) {
            jpql.append(" AND t.trainingDate >= :fromDate");
        }

        if (request.getTo() != null) {
            jpql.append(" AND t.trainingDate <= :toDate");
        }

        if (request.getTraineeName() != null) {
            jpql.append(" AND t.trainee.username = :traineeName");
        }

        TypedQuery<Training> query = em.createQuery(jpql.toString(), Training.class);

        query.setParameter("username", request.getUsername());

        if (request.getFrom() != null) {
            query.setParameter("fromDate", formattedFromDate);
        }

        if (request.getTo() != null) {
            query.setParameter("toDate", formattedToDate);
        }

        if (request.getTraineeName() != null) {
            query.setParameter("trainerName", request.getTraineeName());
        }

        return query.getResultList();
    }

    public List<Trainer> findUnassignedTrainers(String username) {
        return em.createQuery(
                "SELECT tr FROM Trainer tr WHERE NOT EXISTS (SELECT 1 FROM Training t WHERE t.trainer = tr AND t.trainee.username = :username)",
                Trainer.class).setParameter("username", username)
                .getResultList();
    }

    public Optional<Training> findByTraineeUsernameAndDate(String username, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formattedDate = LocalDateTime.parse(date, formatter);
        logger.debug("Finding training for trainee {} at date {}", username, date);
        return em.createQuery(
                "SELECT t FROM Training t WHERE t.trainee.username = :username AND t.trainingDate = :trainingDate",
                Training.class)
                .setParameter("username", username)
                .setParameter("trainingDate", formattedDate)
                .getResultStream()
                .findFirst();
    }

    public List<Training> findAll() {
        return em.createQuery(
                "SELECT t FROM Training t",
                Training.class).getResultList();
    }
}
