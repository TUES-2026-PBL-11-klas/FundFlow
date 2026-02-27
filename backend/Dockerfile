FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]


