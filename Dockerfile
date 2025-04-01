
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY consumer/target/consumer-0.0.1-SNAPSHOT.jar app/consumer-0.0.1-SNAPSHOT.jar

EXPOSE 8081

CMD ["java", "-jar", "app/consumer-0.0.1-SNAPSHOT.jar"]
