# KodemyBackend

This is the backend for Kodemy project (SKNI Kod), written in Spring Boot and Gradle.

## Prerequisites

Before you can run the project, you will need the following applications:

- Java JDK 17
- Docker / Docker Compose
- IntelliJ IDEA (IDE)

## Running project

1. Clone the repository to your local machine.

   ```bash
   git clone https://github.com/skni-kod/KodemyBackend.git
   ```

2. Copy `.env.example` file to `.env`.

3. Execute command below to set up required services:

   ```bash
   docker-compose up -d
   ```

4. Go to local database ([localhost:5432](http://localhost:5432)) with the following credentials and execute the SQL command:

   - **Username:** postgres
   - **Password:** postgres

   ```sql
   CREATE SCHEMA IF NOT EXISTS "kodemy-auth";
   CREATE SCHEMA IF NOT EXISTS "kodemy-backend";
   CREATE SCHEMA IF NOT EXISTS "kodemy-notification";
   ```

5. Run individual services in IntelliJ with the following settings for `Active profiles`:

   - `KodemyAuthApplication`: `local`
   - `KodemyBackendApplication`: `local`
   - `KodemyGatewayApplication`: `local`
   - ~~`KodemyNotificationApplication`: `local`~~
   - `KodemySearchApplication`: `local`

6. Once the project is running, you can access the API documentation (OpenAPI v3) for almost all microservices:

   a. in HTML format, go to `.../swagger-ui/index.html`,
   b. in JSON format, go to `.../v3/api-docs`.

## External services

### RabbitMQ

To access the RabbitMQ management UI, go to [http://localhost:15672](http://localhost:15672) with the following credentials:

- **Username:** rabbitmq
- **Password:** rabbitmq

### OpenSearch

To access the OpenSearch dashboard, go to [http://localhost:5601](http://localhost:5601)