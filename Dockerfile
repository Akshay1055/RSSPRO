# Build the backend from its project subdirectory.
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY swayamsevak_android/ .
RUN chmod +x ./gradlew
RUN ./gradlew :server:installDist --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/server/build/install/server /app
ENV PORT=8080
EXPOSE 8080
CMD ["./bin/server"]
