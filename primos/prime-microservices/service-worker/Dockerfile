# Multi-stage build for service-worker
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and common-lib first (for caching)
COPY pom.xml .
COPY common-lib ./common-lib
COPY service-worker ./service-worker

# Build the application
RUN mvn -pl common-lib install -DskipTests
RUN mvn -pl service-worker package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/service-worker/target/service-worker-0.1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
