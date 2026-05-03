# Gym System - Microservices Architecture

A Gym CRM system built with Spring Boot using a microservices architecture.

This project manages trainees, trainers, and training sessions, and calculates trainer 
workload using a dedicated microservice.

---

## Architecture Overview

This system is composed of **three applications**:

1. **Main Application (Core Business Logic)**
   - Handles trainees, trainers, trainings
   - Manages authentication (JWT)
   - Calls the workload microservice

2. **Workload Microservice**
   - Calculates trainer workload
   - Stores data in-memory
   - Secured using JWT validation

3. **Eureka Server (Service Discovery)**
   - Registers all services
   - Enables communication between microservices

---

## Requirements

Before running the project, make sure your system has:

- Java 17+
- Maven 3.8+
- Git

Verify with:

java -version  
mvn -version  
git --version  

---

## How to Run the Project

IMPORTANT: You must run ALL three applications for the system to work correctly.

---

### 1. Start Eureka Server

cd eureka-server  
mvn spring-boot:run  

Runs on: http://localhost:8761

---

### 2️. Start Workload Microservice

cd workload-microservice  
mvn spring-boot:run  

Example port: http://localhost:8081

---

### 3️. Start Main Application

cd main-app  
mvn spring-boot:run  

Runs on: http://localhost:8080

---

## Author

Randy Miller Rojas Diaz
