# Use the official gradle image to build the application
FROM gradle:7.5.1-jdk17 AS build
WORKDIR /build

# Copy gradle wrapper and project files
COPY build.gradle settings.gradle /build/
COPY src /build/src
COPY gradlew /build/
COPY gradle /build/gradle

# Download dependencies and build the application
RUN ./gradlew build -x test --parallel

# Second stage: runtime
FROM openjdk:17.0-slim
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /build/build/libs/trelloServer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "-Dsun.net.inetaddr.ttl=0", "app.jar"]
