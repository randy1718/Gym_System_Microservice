package com.gym.system.repository;

import java.util.List;
import java.util.Optional;

import com.gym.system.monitoring.metrics.CountTraineesMetric;
import com.gym.system.monitoring.metrics.CountTrainersMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gym.system.model.Trainer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class TrainerDAO {

    @PersistenceContext
    private EntityManager em;

    private final CountTrainersMetric countTrainersMetric;

    private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

    public TrainerDAO(CountTrainersMetric countTrainersMetric) {
        this.countTrainersMetric = countTrainersMetric;
    }

    public void save(Trainer trainer){
        logger.debug("Saving trainer in Database {}", trainer.getUsername());
        em.persist(trainer);
        em.flush();
        countTrainersMetric.increment();
    }

    public Trainer update(Trainer trainer){
        logger.debug("Updating trainer {}", trainer.getUsername());
        return em.merge(trainer);
    }

    public void delete(Long id){
        logger.debug("Deleting trainer {}", id);
        Trainer trainer = em.find(Trainer.class, id);
        if (trainer != null) {
            em.remove(trainer);
        }
    }

    public boolean toggleTrainerStatus(String username) {

        Trainer trainer = em.createQuery(
            "SELECT t FROM Trainer t WHERE t.username = :username",
            Trainer.class
        ).setParameter("username", username)
        .getSingleResult();

        trainer.setIsActive(!trainer.getIsActive());
        return trainer.getIsActive();
    }

    public Optional<Trainer> findByUsername(String username){
        logger.debug("Finding trainer {}", username);
        return em.createQuery(
            "SELECT t FROM Trainer t WHERE LOWER(t.username) = LOWER(:username)",
            Trainer.class
        )
        .setParameter("username", username)
        .getResultStream()
        .findFirst();
    }

    public Boolean activateDeactivateTrainer(String username, Boolean isActive) {

        Trainer trainer = em.createQuery(
            "SELECT t FROM Trainer t WHERE t.username = :username",
            Trainer.class
        ).setParameter("username", username)
        .getSingleResult();

        trainer.setIsActive(isActive);
        return true;
    }

    public List<Trainer> findAll(){
        return em.createQuery(
                "SELECT t FROM Trainer t",
                Trainer.class
        ).getResultList();
    }
}
