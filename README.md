# KodemyBackend

This is the backend for Kodemy project (SKNI Kod), written in Spring Boot and Maven

## Prerequisites

Before you can run the project, you will need the following applications:

- Java JDK 17
- Docker / Docker Compose
- IntelliJ IDEA (IDE)

## Running project

1. Clone the repository to your local machine.
2. Copy the `.env.example` file to `.env`, update the values as needed, and save the file.
    ```bash
    cp .env.example .env
    nano .env
    ```
3. Copy the `application-local.yml.example` file to `application-local.yml`, update the values as needed, and save the file.
   ```bash
   cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
   nano src/main/resources/application-local.yml
    ```
4. To run the project, you have two options:

   a. To run the project without any profile, use the following command:
   ```bash
   docker-compose up -d
   ```
   b. To run the project with the `test` profile and skip steps _5_, _6_, use the following command:
   ```bash
   docker-compose --profile=test up -d
   ```
   This command will run the project with profile `test` which will run Database  (PostgreSQL) and API (Spring Boot) at the same time.
5. To run the project in development mode, set the active profiles in IntelliJ to `dev,local`.
6. Run the project in IntelliJ IDEA.
7. Once the project is running, you can access the API documentation (OpenAPI v3:

   a. in JSON format, go to `http://localhost:8181/api/docs`,

   b. in HTML format, go to `http://localhost:8181/api/docs.html`.