
# Guidely

**Guidely** is a ticket management system built with Spring Boot. It handles ticket creation, assignment, and notifications while enforcing business rules such as duplicate title validation. The project leverages modern technologies and DevOps practices to ensure quality and maintainability.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
  - [Locally via Maven](#locally-via-maven)
  - [Using Docker Compose](#using-docker-compose)
- [Testing](#testing)
- [SonarQube Analysis](#sonarqube-analysis)
- [API Documentation](#api-documentation)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Features

- **Ticket Operations:** Create, update, delete, and reassign tickets.
- **Duplicate Validation:** Prevents creation or update of tickets with duplicate titles.
- **Agent Assignment:** Automatically assigns tickets to support agents using a least busy algorithm.
- **Notifications:** Sends email notifications via RabbitMQ.
- **API Documentation:** Swagger/OpenAPI integration for interactive API docs.
- **Code Quality:** Integrated Jacoco for code coverage and SonarQube for static analysis.

## Technologies

- **Java 17**
- **Spring Boot 3.4.1**
- **Spring Data JPA & Security**
- **PostgreSQL**
- **RabbitMQ**
- **Liquibase**
- **Maven**
- **Swagger/OpenAPI (springdoc-openapi)**
- **Jacoco (Test Coverage)**
- **SonarQube (Code Analysis)**

## Prerequisites

- **Java 17 JDK** installed.
- **Maven** installed.
- **Docker & Docker Compose** installed.

## Installation

1. **Clone the Repository:**

   ```bash
   git clone <repository-url>
   cd Guidely
   ```

2. **Configure the Application:**

   Adjust the configuration in `application.properties` or `application.yml` as needed for your local environment (e.g., database URL, RabbitMQ settings).

3. **Build the Project:**

   ```bash
   mvn clean install
   ```

## Running the Application

### Locally via Maven

To run the application locally, use the following Maven command:

```bash
mvn spring-boot:run
```

The application will start on port **8080** by default.

### Using Docker Compose

The project includes a Docker Compose file that runs the required infrastructure:

- **PostgreSQL** (Guidely Database) on port **5434**
- **RabbitMQ** on ports **5672** (AMQP) and **15672** (Management UI)
- **SonarQube** on port **9000** (with its dedicated PostgreSQL on port **5433**)

To start all services, run:

```bash
docker-compose up -d
```

**Access:**

- **PostgreSQL:** `localhost:5434`
- **RabbitMQ Management:** [http://localhost:15672](http://localhost:15672) (default credentials: guest/guest)
- **SonarQube:** [http://localhost:9000](http://localhost:9000)

## Testing

To run unit tests and generate a Jacoco code coverage report, execute:

```bash
mvn clean verify
```

After tests complete, the Jacoco report is available in the `target/site/jacoco` directory.

## SonarQube Analysis

Ensure that SonarQube is running (via Docker Compose) at [http://localhost:9000](http://localhost:9000). Then, run the following Maven command to perform a SonarQube analysis:

```bash
mvn clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=your_sonar_token
```

Replace `your_sonar_token` with your actual SonarQube authentication token.

## API Documentation

Once the application is running, you can access the Swagger UI to explore and test the API endpoints:

```
http://localhost:8080/swagger-ui/index.html
```

This interactive documentation provides details on each API endpoint, request/response models, and allows you to try out API calls directly from your browser.

## Troubleshooting

- **Docker Issues:**
   - Ensure Docker and Docker Compose are installed and running.
   - Run `docker-compose ps` to verify that all containers are healthy.
   - Check container logs with `docker-compose logs <service-name>`.

- **Application Issues:**
   - Review the console output for any error messages.
   - Validate that the configuration properties are correct for your environment.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Open a Pull Request for review.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or support, please contact:

- **Name:** Your Name
- **Email:** [your.email@example.com](mailto:your.email@example.com)

