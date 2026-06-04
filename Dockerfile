# First stage: Build the application using Maven
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the JAR (skip tests for speed, but you can keep them)
RUN mvn clean package -DskipTests

# Second stage: Create the final lightweight image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/LMSPrototype-1.0-SNAPSHOT.jar app.jar

# Expose port (if your app uses a web server; for console app it's not needed but harmless)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]