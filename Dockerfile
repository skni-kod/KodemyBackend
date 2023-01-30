FROM maven:3.8.6-openjdk-18-slim AS MAVEN_BUILD
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip

FROM openjdk:17.0.2-slim-buster
EXPOSE 8080
COPY --from=MAVEN_BUILD /target/kodemy*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]