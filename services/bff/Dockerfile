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
