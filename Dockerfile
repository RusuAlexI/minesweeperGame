FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY . /app

# Build the app
RUN ./mvnw package -DskipTests

# Run the app
CMD ["java", "-jar", "target/minesweeper-backend-0.0.1-SNAPSHOT.jar"]
