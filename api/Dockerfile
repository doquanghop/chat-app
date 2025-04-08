FROM gradle:8.10-jdk23 AS builder

WORKDIR /app

COPY . .

RUN gradle build -x test

FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]