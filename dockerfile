# Use JDK 17
FROM eclipse-temurin:21-jdk-alpine


WORKDIR /app

# Copy Maven wrapper and project
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Make mvnw executable
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "target/neuroguard-0.0.1-SNAPSHOT.jar"]
