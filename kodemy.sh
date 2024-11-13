cd /home/kodemy/KodemyBackend/commons
./gradlew build

cd ../kodemy-api-gateway
./gradlew build -x test
cd ../kodemy-auth
./gradlew build -x test
cd ../kodemy-backend
./gradlew build -x test
cd ../kodemy-notification
./gradlew build -x test
cd ../kodemy-search
./gradlew build -x test
cd ../kodemy-service-registry
./gradlew build -x test
cd ../

docker compose -f docker-compose.stack.yml up --build