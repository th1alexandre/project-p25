# Stage 1: Create a Java runtime
FROM eclipse-temurin:17 AS jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime


# Stage 2: Build the application
FROM eclipse-temurin:17 AS builder

# Create a non-root user for running the application
RUN useradd -u 1001 nonroot

# Set working directory
WORKDIR /app

# Copy all the necessary files for the build
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests


# Stage 3: Final image
FROM gcr.io/distroless/static-debian12 AS runtime

# Set the JAVA_HOME environment variable
ENV JAVA_HOME=/opt/java/openjdk

# Add the Java runtime to the PATH
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the custom Java runtime from the jre-build stage
COPY --from=jre-build /javaruntime $JAVA_HOME

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
ENTRYPOINT ["java", "-jar", "/app/api.jar"]
