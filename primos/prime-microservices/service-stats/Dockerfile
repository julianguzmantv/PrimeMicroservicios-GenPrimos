# Multi-stage build for service-stats
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and common-lib first (for caching)
COPY pom.xml .
COPY common-lib ./common-lib
COPY service-stats ./service-stats

# Build the application
RUN mvn -pl common-lib install -DskipTests
RUN mvn -pl service-stats package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/service-stats/target/service-stats-0.1.0.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
