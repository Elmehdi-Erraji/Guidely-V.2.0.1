# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container
# (Assumes that your jar is built at target/Guidely-0.0.1-SNAPSHOT.jar)
ARG JAR_FILE=target/Guidely-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose the port that your Spring Boot app listens on (default is 8080)
EXPOSE 4040

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
