# Use JDK 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Maven wrapper and project
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build the project
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/neuroguard-0.0.1-SNAPSHOT.jar"]
