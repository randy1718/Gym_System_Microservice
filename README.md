# Gym System - Microservices Architecture with Asynchronous Messaging

A Gym CRM system built with Spring Boot using a microservices architecture.

This project manages trainees, trainers, and training sessions, and calculates trainer workload using a dedicated microservice with asynchronous communication through ActiveMQ.

---

# Architecture Overview

This system is composed of **four components**:

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
- Stores workload data in-memory
- Processes asynchronous events

Example port:

- `http://localhost:8081`

---

## 3. Eureka Server (Service Discovery)

Responsibilities:

- Registers microservices
- Enables service discovery between applications

Runs on:

- `http://localhost:8761`

---

## 4. ActiveMQ Broker (Message Broker)

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
5. Trainer workload is calculated asynchronously

Benefits:

- Better scalability
- Better fault tolerance
- Loose coupling between services
- Faster API responses
- Retry capability through queues

---

# Technologies Used

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring JMS
- Apache ActiveMQ
- PostgreSQL
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

The project uses Docker to run the ActiveMQ broker.

## Pull ActiveMQ Image

```bash
docker pull rmohr/activemq
```

## Run ActiveMQ Container

```bash
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq
```

## Verify Running Containers

```bash
docker ps
```

Expected container:

```text
activemq
```

## Stop ActiveMQ Container

```bash
docker stop activemq
```

## Start Existing Container Again

```bash
docker start activemq
```

## Remove Container

```bash
docker rm -f activemq
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

If not already running:

```bash
docker start activemq
```

Or create the container for the first time:

```bash
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  rmohr/activemq
```

---

# 2. Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

Runs on:

```text
http://localhost:8761
```

---

# 3. Start Workload Microservice

```bash
cd workload-microservice
mvn spring-boot:run
```

Example port:

```text
http://localhost:8081
```

This microservice listens to:

```text
workload.queue
```

using:

```java
@JmsListener(destination = "workload.queue")
```

---

# 4. Start Main Application

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

---

# Author

Randy Miller Rojas Diaz