FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY ../pom.xml .
COPY . .

RUN mvn clean package -DskipTests -pl consumer -am

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/consumer/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
