FROM openjdk:17.0.2-slim-buster AS runtime
EXPOSE 8761
COPY build/libs/kodemy-service-registry.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
