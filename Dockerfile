FROM openjdk:21-jdk-slim

COPY target/ /app
EXPOSE 8080

CMD ["java", "-jar", "/app/Medals-Backend-0.0.1-SNAPSHOT.jar"]