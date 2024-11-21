# Stage 1: Build the application
FROM eclipse-temurin:17 AS builder

# Create a non-root user for running the application
RUN useradd -u 1001 nonroot

# Set working directory
WORKDIR /app

# Copy all the necessary files for the build
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests


# Stage 2: Final image
FROM gcr.io/distroless/java17-debian12 AS runtime

# Set working directory
WORKDIR /

# Copy the /etc/passwd file from the build stage to provide non-root user information
COPY --from=builder /etc/passwd /etc/passwd

# Copy the built artifact from the builder stage
COPY --from=builder /app/target/*.jar /app/api.jar

# Use the non-root user created in the build stage
USER nonroot

# Expose the port the application will run on
EXPOSE 8080

# Define the command to run the application when the container starts
CMD ["/app/api.jar"]
