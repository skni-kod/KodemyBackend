FROM gradle:7.4.0-jdk17 AS GRADLE_BUILD
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle build.gradle .
RUN gradle dependencies --no-daemon
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test

FROM openjdk:17.0.2-slim-buster
EXPOSE 8080
COPY --from=GRADLE_BUILD /home/gradle/project/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

