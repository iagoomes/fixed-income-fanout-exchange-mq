FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY producer/ producer/
COPY consumer/ consumer/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG MODULE=producer

COPY --from=builder /app/${MODULE}/target/*.jar app.jar

EXPOSE 8080 8081 8082 8083 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
