FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/ /app
EXPOSE 8080

CMD ["java", "-jar", "/Medals-Backend-0.0.1-SNAPSHOT.jar"]