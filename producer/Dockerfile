FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY ../pom.xml .
COPY . .

RUN mvn clean package -DskipTests -pl producer -am

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/producer/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
