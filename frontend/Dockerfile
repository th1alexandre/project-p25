# Stage 1: Build the React application
FROM node:20 AS builder

# Create a non-root user for running the application
RUN useradd -u 1001 nonroot

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the app's source code
COPY . .

# Build the React app
RUN npm run build


# Stage 2: Final image with build artifacts only
FROM node:20-alpine AS runtime

# Copy the /etc/passwd file from the build stage to provide non-root user information
COPY --from=builder /etc/passwd /etc/passwd

# Set working directory
WORKDIR /

# Copy build output from the builder stage
COPY --from=builder /app/build ./build

# Create home directory for the non-root user
RUN mkdir /home/nonroot

# Gives ownership of the home directory to the non-root user
RUN chown -R nonroot /home/nonroot

# Use the non-root user created in the build stage
USER nonroot

# Expose the port used by the app (optional)
EXPOSE 3000

# Default command
CMD ["npx", "serve", "-s", "build", "-l", "3000"]
