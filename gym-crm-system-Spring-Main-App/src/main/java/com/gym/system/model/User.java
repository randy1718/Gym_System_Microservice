package com.gym.system.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String lastName;

    @Column(nullable = false, unique = true)
    protected String username;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    protected String rawPassword;

    @Column(nullable = false)
    protected boolean isActive;

    @Column
    protected int failedAttempts;

    @Column
    protected LocalDateTime lockTime;
}
