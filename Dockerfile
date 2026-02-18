# ────────────────────────────────────────────────
# STAGE 1: BUILD
# Uses the official Maven image with JDK 17
# This stage compiles the project and creates the JAR
# ────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml first — Docker caches this layer.
# If pom.xml doesn't change, dependencies are NOT re-downloaded on next build.
COPY pom.xml .

# Download all dependencies (cached separately from source code)
RUN mvn dependency:go-offline -B

# Now copy the source code
COPY src ./src

# Build the JAR, skipping tests (tests should run in CI/CD, not Docker build)
RUN mvn clean package -DskipTests

# ────────────────────────────────────────────────
# STAGE 2: RUNTIME
# Uses a minimal JRE (not full JDK) — much smaller image
# Only the compiled JAR is copied here, not Maven or source code
# ────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create a dedicated non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy only the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to our non-root user
RUN chown appuser:appgroup app.jar

# Switch to non-root user (security best practice)
USER appuser

# Expose the port Spring Boot listens on
EXPOSE 8080

# JVM tuning for containers — prevents Spring Boot from using too much memory
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]