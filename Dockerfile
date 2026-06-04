# First stage: Build the JAR
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Second stage: Run the JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/LMSPrototype-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]