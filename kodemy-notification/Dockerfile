FROM openjdk:17.0.2-slim-buster AS runtime
EXPOSE 8080
COPY build/libs/kodemy-notification.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
