package com.gym.system.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "trainee_id",
        nullable = false,
        foreignKey = @ForeignKey(
        name = "fk_training_trainee",
        foreignKeyDefinition = 
            "FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE"
        )
    )
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "trainer_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_training_trainer")
    )
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "trainingType_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_training_trainingType")
    )
    private TrainingType trainingType;

    @Column(nullable = false)
    private LocalDateTime trainingDate;

    @Column(nullable = false)
    private int trainingDuration;


    public Long getId() {
        return id;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee){
        this.trainee = trainee;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer){
        this.trainer = trainer;
    }

    public String getTrainingName(){
        return trainingName;
    }

    public void setTrainingName(String trainingName){
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType(){
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType){
        this.trainingType = trainingType;
    }

    public String getDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = trainingDate.format(formatter);
        return formattedDate;
    }

    public LocalDateTime getDatetime(){
        return trainingDate;
    }

    public void setDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.trainingDate = LocalDateTime.parse(date, formatter);
    }

    public int getDuration() {
        return trainingDuration;
    }

    public void setDuration(int duration){
        this.trainingDuration = duration;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id='" + id + '\'' +
                ", traineeId='" + trainee.getId() + '\'' +
                ", trainerId='" + trainer.getId() + '\'' +
                ", trainingType=" + trainingType.getName() +
                ", dateTime=" + trainingDate +
                ", duration=" + trainingDuration + " min" +
                '}';
    }
}
