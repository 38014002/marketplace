# Dockerfile reutilizable para microservicios Spring Boot
# Uso: docker build --build-arg SERVICE_DIR=ms-user -t marketplace-ms-user .

FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE_DIR
WORKDIR /app
COPY ${SERVICE_DIR}/pom.xml .
COPY ${SERVICE_DIR}/.mvn .mvn
COPY ${SERVICE_DIR}/mvnw .
COPY ${SERVICE_DIR}/src ./src
RUN chmod +x mvnw && ./mvnw -q package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE_DIR
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
