FROM gradle:8.8.0-jdk17 AS gradle_build
COPY --chown=gradle:gradle build.gradle settings.gradle /home/gradle/project/
COPY --chown=gradle:gradle gradle /home/gradle/project/gradle
WORKDIR /home/gradle/project
RUN gradle dependencies --no-daemon

COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build --no-daemon -x test

FROM openjdk:17.0.2-slim-buster AS runtime
EXPOSE 8080
COPY --from=gradle_build /home/gradle/project/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]