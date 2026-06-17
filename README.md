# Gym System - Microservices Architecture with Cucumber Tests

A Gym CRM system built with Spring Boot using a microservices architecture.

This project manages trainees, trainers, and training sessions, and calculates trainer workload using a dedicated microservice with asynchronous communication through ActiveMQ.

---

# Architecture Overview

This system is composed of **five components**:

## 1. Main Application (Core Business Logic)

Responsibilities:

- Handles trainees, trainers, and trainings
- Manages authentication using JWT
- Produces workload events to ActiveMQ queues
- Uses PostgreSQL database

Runs on:

- `http://localhost:8080`

---

## 2. Workload Microservice

Responsibilities:

- Consumes workload messages from ActiveMQ
- Calculates trainer workload
- Stores workload data in MongoDB
- Processes asynchronous events

Example port:

- `http://localhost:8081`

Database:

- `gym_workload_db`

---

## 3. MongoDB

Responsibilities:

- Persists trainer workload information
- Stores aggregated workload records
- Provides durable storage for workload calculations

Default Port:

- `27017`

Database:

```text
gym_workload_db
```

---

## 4. Eureka Server (Service Discovery)

Responsibilities:

- Registers microservices
- Enables service discovery between applications

Runs on:

- `http://localhost:8761`

---

## 5. ActiveMQ Broker (Message Broker)

Responsibilities:

- Handles asynchronous messaging between services
- Stores and delivers workload events
- Decouples the main app from the workload microservice

Ports:

- `61616` → JMS broker
- `8161` → ActiveMQ dashboard

Dashboard:

- `http://localhost:8161`

Default credentials:

- Username: `admin`
- Password: `admin`

---

# Asynchronous Messaging Flow

The system uses **ActiveMQ queues** for asynchronous communication.

Flow:

1. A training is created in the Main Application
2. The Main Application sends a message to:
   - `workload.queue`
3. ActiveMQ stores the message
4. The Workload Microservice consumes the message
5. Trainer workload is calculated
6. Workload data is persisted in MongoDB

Benefits:

- Better scalability
- Better fault tolerance
- Loose coupling between services
- Faster API responses
- Persistent workload storage
- Retry capability through queues

---

# Data Persistence

The Workload Microservice stores trainer workload information in MongoDB.

Database:

```text
gym_workload_db
```

Example document structure:

```json
{
  "username": "trainer1",
  "firstName": "John",
  "lastName": "Doe",
  "active": true,
  "years": {
    "2026": {
      "JANUARY": 12,
      "FEBRUARY": 8
    }
  }
}
```

---

# Technologies Used

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring JMS
- Apache ActiveMQ
- PostgreSQL
- MongoDB
- Spring Data MongoDB
- Eureka Server
- Maven
- Docker

---

# Requirements

Before running the project, make sure your system has:

- Java 17+
- Maven 3.8+
- Docker Desktop
- Git

Verify installations:

```bash
java -version
mvn -version
docker --version
git --version
```

---

# Docker Configuration

The project uses Docker to run both ActiveMQ and MongoDB.

## Pull ActiveMQ Image

```bash
docker pull rmohr/activemq
```

## Pull MongoDB Image

```bash
docker pull mongo
```

---

## Run ActiveMQ Container

```bash
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq
```

---

## Run MongoDB Container

```bash
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  mongo
```

---

## Verify Running Containers

```bash
docker ps
```

Expected containers:

```text
activemq
mongodb
```

---

## Stop Containers

```bash
docker stop activemq
docker stop mongodb
```

---

## Start Existing Containers Again

```bash
docker start activemq
docker start mongodb
```

---

## Remove Containers

```bash
docker rm -f activemq
docker rm -f mongodb
```

---

# MongoDB Configuration

Example configuration for the Workload Microservice:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=gym_workload_db
```

Alternatively:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/gym_workload_db
```

---

# ActiveMQ Dashboard

Dashboard URL:

```text
http://localhost:8161
```

Credentials:

```text
Username: admin
Password: admin
```

---

# How to Run the Project

IMPORTANT:

You must run ALL components for the system to work correctly.

---

# 1. Start ActiveMQ Broker

```bash
docker start activemq
```

or create it:

```bash
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq
```

---

# 2. Start MongoDB

```bash
docker start mongodb
```

or create it:

```bash
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  mongo
```

Verify:

```bash
docker ps
```

---

# 3. Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8761
```

---

# 4. Start Workload Microservice

```bash
cd workload-microservice
mvn spring-boot:run
```

Example port:

```text
http://localhost:8081
```

This microservice:

- Listens to `workload.queue`
- Processes workload events
- Persists workload data into MongoDB (`gym_workload_db`)

Listener example:

```java
@JmsListener(destination = "workload.queue")
```

---

# 5. Start Main Application

```bash
cd main-app
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8080
```

The application publishes workload events using:

```java
jmsTemplate.convertAndSend("workload.queue", request);
```

---

# ActiveMQ Configuration

Example configuration:

```properties
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin
```

---

# Example Queue

Queue used in this project:

```text
workload.queue
```

---

# Verifying MongoDB Data

Connect to the MongoDB container:

```bash
docker exec -it mongodb mongosh
```

Select the database:

```javascript
use gym_workload_db
```

View stored workload documents:

```javascript
db.trainerWorkloads.find().pretty()
```

---

# Optional Improvements

Possible future enhancements:

- Dead Letter Queue (DLQ)
- Retry mechanism
- Message persistence
- Distributed tracing
- Docker Compose setup
- Kubernetes deployment
- Centralized logging
- Monitoring with Prometheus + Grafana
- MongoDB replica sets
- MongoDB indexing strategy

---

# Author

Randy Miller Rojas Diaz