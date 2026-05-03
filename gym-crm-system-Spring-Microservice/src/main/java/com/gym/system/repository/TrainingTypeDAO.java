package com.gym.system.repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.gym.system.model.TrainingType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TrainingTypeDAO {

    @PersistenceContext
    private EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);

    public TrainingTypeDAO() {
    }

    public void save(TrainingType trainingType){
        logger.debug("Saving training type in Database {}", trainingType.getName());
        em.persist(trainingType);
        em.flush();
    }

    public void delete(String id){
        logger.debug("Deleting training type {}", id);
        TrainingType trainingType = em.find(TrainingType.class, id);
        if (trainingType != null) {
            em.remove(trainingType);
        }
    }

    public Optional<TrainingType> findByName(String name){
        logger.debug("Finding training type {}", name);
        return em.createQuery(
            "SELECT t FROM TrainingType t WHERE LOWER(t.name) = LOWER(:name)",
            TrainingType.class
        )
        .setParameter("name", name)
        .getResultStream()
        .findFirst();
    }

    public List<TrainingType> findAll(){
        return em.createQuery(
                "SELECT t FROM TrainingType t",
                TrainingType.class
        ).getResultList();
    }
}
