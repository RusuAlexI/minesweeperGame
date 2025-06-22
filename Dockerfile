# Stage 1: Build the application
FROM openjdk:17-jdk-slim as builder
WORKDIR /app
COPY . .
# Explicitly make mvnw executable inside the Docker container
RUN chmod +x mvnw
# Use mvnw (Maven Wrapper) to ensure consistent builds
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final Docker image
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080
# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]