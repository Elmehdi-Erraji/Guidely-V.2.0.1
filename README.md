
# 🚀 Guidely - The Smart Ticketing System 🎟️

**Guidely** is a **modern ticket management system** built with **Spring Boot**. It streamlines **ticket creation, assignment, and notifications** while ensuring **quality and maintainability** with DevOps best practices. Oh, and did we mention **real-time updates with WebSockets?** 🚀  

Need a **reliable and scalable** helpdesk system? **Guidely’s got you covered!** 🎯  

---

## 🗂️ Table of Contents  

- [🔥 Features](#-features)  
- [🛠️ Technologies](#️-technologies)  
- [📌 Prerequisites](#-prerequisites)  
- [⚙️ Installation](#️-installation)  
- [🚀 Running the Application](#-running-the-application)  
  - [Locally via Maven](#locally-via-maven)  
  - [Using Docker Compose](#using-docker-compose)  
- [✅ Testing](#-testing)  
- [📊 SonarQube Analysis](#-sonarqube-analysis)  
- [🔄 Real-time WebSocket Updates](#-real-time-websocket-updates)  
- [🔧 Jenkins Pipeline](#-jenkins-pipeline)  
- [📖 API Documentation](#-api-documentation)  
- [💡 Troubleshooting](#-troubleshooting)  
- [🤝 Contributing](#-contributing)  
- [📝 License](#-license)  
- [📬 Contact](#-contact)  

---

## 🔥 Features  

✅ **Ticket Operations:** Create, update, delete, and reassign tickets effortlessly.  
✅ **Duplicate Protection:** Stops duplicate tickets in their tracks.  
✅ **Smart Agent Assignment:** Uses the **least-busy-first** algorithm to assign tickets.  
✅ **WebSocket-powered Real-Time Updates:** Get instant ticket status updates with **no page refresh!** 🔄  
✅ **Email Notifications:** Integrated **RabbitMQ messaging system** sends ticket updates straight to your inbox.  
✅ **Swagger API Docs:** Built-in, interactive API documentation.  
✅ **Top-Notch Code Quality:** Thanks to **Jacoco (code coverage) & SonarQube (static analysis).**  

---

## 🛠️ Technologies  

🚀 **Java 17**  
🛠️ **Spring Boot 3.4.1**  
🔐 **Spring Security & JWT**  
🐘 **PostgreSQL**  
📩 **RabbitMQ for messaging**  
🗂 **Liquibase (DB migrations)**  
📜 **Swagger/OpenAPI (Docs)**  
📈 **Jacoco (Test Coverage)**  
🧪 **SonarQube (Code Analysis)**  
📡 **Spring WebSockets for real-time updates**  

---

## 📌 Prerequisites  

Before you jump in, make sure you have:  

🔹 **Java 17** installed  
🔹 **Maven** installed  
🔹 **Docker & Docker Compose** installed  

---

## ⚙️ Installation  

1️⃣ **Clone the Repository:**  

```bash
git clone <repository-url>
cd Guidely
```

2️⃣ **Configure the Application:**

Edit `application.properties` or `application.yml` for local DB and RabbitMQ settings.

3️⃣ **Build the Project:**

```bash
mvn clean install
```

---

## 🚀 Running the Application

### 🖥️ Locally via Maven

Run the app with:

```bash
mvn spring-boot:run
```

The app launches on **port 8080**! 🚀

### 🐳 Using Docker Compose

To start all services (PostgreSQL, RabbitMQ, SonarQube), run:

```bash
docker-compose up -d
```

**Access Services:**

🔹 PostgreSQL: `localhost:5434`  
🔹 RabbitMQ UI: [http://localhost:15672](http://localhost:15672) (guest/guest)  
🔹 SonarQube: [http://localhost:9000](http://localhost:9000)

---

## ✅ Testing

Run unit tests & generate code coverage reports:

```bash
mvn clean verify
```

Coverage reports will be in `target/site/jacoco`.

---

## 📊 SonarQube Analysis

Make sure SonarQube is running at **[http://localhost:9000](http://localhost:9000)**, then run:

```bash
mvn clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=your_sonar_token
```

Replace `your_sonar_token` with your actual SonarQube token.

---

## 🔄 Real-time WebSocket Updates

Guidely supports **real-time updates** via WebSockets! 🛰

Want **instant ticket status updates** without refreshing the page? Just subscribe to:

📡 **WebSocket Endpoint:**

```
ws://localhost:8080/ws/tickets
```

🔹 **How it Works:**

- When a new ticket is created or updated, all connected users receive instant updates.
- No more manual refreshes—stay up-to-date in real-time! 🚀

**Example:**

```javascript
const socket = new WebSocket("ws://localhost:8080/ws/tickets");

socket.onmessage = (event) => {
    console.log("New ticket update:", event.data);
};
```

---

## 🔧 Jenkins Pipeline

Automate build, analysis, and deployment with this **Jenkinsfile**:

```groovy
pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
        DOCKER_IMAGE = "mehdi02/guidely:latest"
        SONAR_TOKEN = credentials('sonar-token')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'dev', url: 'https://github.com/Elmehdi-Erraji/Guidely-V.2.0.1'
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh "mvn sonar:sonar -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    sh "echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin"
                    sh "docker build -t ${DOCKER_IMAGE} ."
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }
    }
}
```

---

## 📖 API Documentation

Access **Swagger UI** here:

```
http://localhost:8080/swagger-ui/index.html
```

Interactive, self-documented API FTW! 🎯

---

## 💡 Troubleshooting

**Docker Issues?**
- Run `docker-compose ps` to check service health.
- Use `docker-compose logs <service-name>` for debugging.

**Application Issues?**
- Check error logs in the console.
- Verify config files (`application.yml`).

---

## 🤝 Contributing

Contributions are **welcome!** 🚀

1. Fork the repo
2. Create a feature branch
3. Commit your changes
4. Open a **Pull Request**

---

## 📝 License

**MIT License** – Use, modify, and distribute freely! 🏆

---

## 📬 Contact

📌 **Author:** Mehdi Erraji  
📧 **Email:** [elmehdi-erraji@hotmail.com](mailto:elmehdi-erraji@hotmail.com)  

---

Now go ahead and **launch** Guidely! 🚀💡🎟

