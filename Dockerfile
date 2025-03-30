FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar
COPY firebase-adminsdk.json firebase-adminsdk.json

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 