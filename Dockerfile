FROM gradle:7.4.0-jdk17 AS GRADLE_BUILD
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build --no-daemon

FROM openjdk:17.0.2-slim-buster
EXPOSE 8080
COPY --from=GRADLE_BUILD /home/gradle/project/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]