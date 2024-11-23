FROM openjdk:17.0.2-slim-buster AS runtime
EXPOSE 8080
COPY build/libs/kodemy-api-gateway.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
