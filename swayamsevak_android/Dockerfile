# Build Stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY . .
# Make gradlew executable
RUN chmod +x ./gradlew
# Build the server distribution
RUN ./gradlew :server:installDist --no-daemon

# Run Stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the built distribution from the build stage
COPY --from=build /app/server/build/install/server /app
# Set environment variables
ENV PORT=8080
EXPOSE 8080
# Run the server
CMD ["./bin/server"]
