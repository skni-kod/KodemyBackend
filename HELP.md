## Instrukcja uruchamiania KodemyBackend

### 1. Środowisko Docker

Wykonać komendę `docker compose up -d` (lub `docker-compose up -d`). Wymagane zainstalowanie `docker` i `compose` (lub `docker` i `docker-compose`).

### 2. Baza danych

W bazie danych (domyślnie `localhost:5435`, baza `kodemy`, user `postgres`, hasło `postgres`) utworzyć schematy:
- `kodemy-auth`
- `kodemy-backend`
- `kodemy-notification`

### 3. Serwisy

Wymagane zainstalowanie `gradle`. 

Gdy IntelliJ nie zaindeksuje automatycznie, należy:
- Zbudować każdy z serwisów komendą `./gradlew build`
- Ustawić w każdym serwisie profil `local`
- Uruchomić serwisy