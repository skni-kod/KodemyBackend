# KodemyBackend

This is the backend for Kodemy project (SKNI Kod), written in Spring Boot and Gradle.

## Prerequisites

Before you can run the project, you will need the following applications:

- Java JDK 17
- Docker / Docker Compose
- IntelliJ IDEA (IDE)

## Running project

1. Clone the repository to your local machine.
2. To run the project, you have two options:

   a. To run the project without any profile, use the following command:
   ```bash
   docker-compose up -d
   ```
3. To run the project in development mode, set the active profiles in IntelliJ to `local` in all Intellij
   configurations.
4. Run the project.
5. Once the project is running, you can access the API documentation (OpenAPI v3) for almost all microservices:

   a. in HTML format, go to `.../swagger-ui/index.html`,

   b. in JSON format, go to `.../v3/api-docs`.

## Migrate database (suspended)

1. Copy `db.sql` to `sql` directory.
2. To migrate database, use the following commands:
   ```bash
   docker exec -it kodemy_postgres_dev /bin/bash
   psql -U root -W -d kodemy < sql/db.sql
   ```