# Stage 1: Build
FROM golang:1.23-bookworm AS builder

# Set the GIN_MODE environment variable to release
ENV GIN_MODE=release

# Create a non-root user for running the application
RUN useradd -u 1001 nonroot

# Set working directory
WORKDIR /app

# Copy Go modules files first for dependency caching
COPY src/go.mod src/go.sum ./

# Use cache mounts to speed up the installation of existing dependencies
RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    go mod download

# Copy the application source code
COPY src/ .

# Compile the application during build and statically link the binary
RUN go build \
    -ldflags="-linkmode external -extldflags -static" \
    -tags netgo \
    -o go-bff


# Stage 2: Final image
FROM gcr.io/distroless/base-debian12 AS runtime

# Set working directory
WORKDIR /

# Copy the /etc/passwd file from the build stage to provide non-root user information
COPY --from=builder /etc/passwd /etc/passwd

# Copy the compiled application binary from the builder stage to the final image
COPY --from=builder /app/go-bff /go-bff

# Use the non-root user created in the build stage
USER nonroot

# Expose the port the application will run on
EXPOSE 8085

# Define the command to run the application when the container starts
CMD ["./go-bff"]
