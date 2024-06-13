# Use the official gradle image to build the application
FROM gradle:7.5.1-jdk17 AS build
WORKDIR /build

# Copy gradle files and download dependencies
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# Build the application
COPY . /build
RUN gradle build -x test --parallel

# APP
FROM openjdk:17.0-slim
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /build/build/libs/beer-backend-0.0.1-SNAPSHOT.jar app.jar
COPY upload /app/upload
EXPOSE 8080

# Run the application
ENTRYPOINT [ "java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "-Dsun.net.inetaddr.ttl=0", "app.jar" ]
