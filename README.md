# Hydra

Hydra is a locally deployed smart-plant monitoring system. It connects
Raspberry Pi Pico W devices to capacitive soil-moisture sensors, and stores
readings in a relational database, and provides the user with a browser dashboard for
managing plants and managing household members.

When a plant crosses its configured minimum moisture threshold, Hydra sends a
notification email to the household. The dashboard also refreshes plant data
periodically.

## Contents

- [Features](#features)
- [Architecture](#architecture)
- [Hardware](#hardware)
- [Technology stack](#technology-stack)
- [Running locally](#running-locally)
- [Project status and notes](#project-status-and-notes)

## Features

- Creating an account and authenticating with a server-side HTTP session.
- Creating or joining a household using an invite code.
- Adding, editing, and deleting plant profiles.
- Pairing one sensor device with each plant using a unique device ID.
- Uploading plant images to Filebase (S3-compatible object storage).
- Viewing presigned plant-image URLs in the dashboard.
- Storing moisture readings and optional temperature readings with timestamps.
- Converting raw sensor values into a moisture percentage for display.
- Sending one email when a plant enters a low-moisture state rather than sending
  repeated alerts for every reading while it remains below the threshold.

## Architecture

Hydra is a Spring Boot MVC application with a static frontend served by the
same backend:

- **Controllers** expose REST endpoints for accounts, households, plants, and
  sensor ingestion.
- **Services** contain business logic for authentication, household and plant
  management, sensor processing, image storage, and notifications.
- **Spring Data JPA/Hibernate** maps users, households, plants, and readings to
  PostgreSQL tables.
- **The frontend** uses HTML, CSS, and vanilla JavaScript. It communicates with
  the backend using `fetch` and session cookies.
- **Filebase S3-compatible storage** stores plant images. The backend stores
  object keys and generates presigned download URLs when needed.
- **SMTP/Gmail-compatible mail delivery** sends low-moisture notifications.

The main source directories are:

```text
backend/
├── src/main/java/com/example/hydra/
│   ├── config/        # S3, CORS, and password-encoding configuration
│   ├── controller/    # REST endpoints
│   ├── dto/           # Request and sensor payload objects
│   ├── entity/        # JPA entities
│   ├── repository/    # Spring Data repositories
│   └── service/       # Application and integration services
├── src/main/resources/
│   ├── static/        # HTML, CSS, and JavaScript frontend
│   └── application*.properties
└── src/test/          # Controller, service, and integration tests
```

## Hardware

The current hardware setup uses:

- Raspberry Pi Pico W
- Adafruit STEMMA capacitive soil-moisture sensor
- A plant
- Wi-Fi access shared by the Pico and the Hydra backend


The firmware can be found in the ```firmware/``` folder. 

The Pico firmware is written in CircuitPython. You can configure the Wi-Fi
credentials in the Pico's `settings.toml`, then update `code.py` with the
backend computer's LAN IP address. The firmware sends readings to
`POST /api/sensor`. 

The sensor payload uses this shape:

```json
{
  "device_id": "Pico01",
  "moisture": 850,
  "temperature": 21.5
}
```

`device_id` is normalized for case-insensitive pairing. Moisture readings must
be in the raw range `200` to `2000`; Hydra maps that range to `0%` to `100%`
for dashboard status and notification logic.

## Technology stack

- Java 17 
- Spring Boot 4.0.7
- Gradle 9.5.1
- Spring Web MVC
- Spring Data JPA and Hibernate
- PostgreSQL for application data
- H2 for tests
- Spring Mail
- AWS SDK for S3-compatible object storage (Filebase)
- BCrypt password hashing
- HTML, CSS, and vanilla JavaScript


## Running locally

The default profile reads PostgreSQL settings from environment variables.

From the `backend` directory, set the database variables in the current
terminal session and start the application with the local profile:

```powershell
cd backend
$env:DB_URL = "jdbc:postgresql://localhost:port/hydra"
$env:DB_USERNAME = "<database username>"
$env:DB_PASSWORD = "<database password>"
$env:SPRING_PROFILES_ACTIVE = “local”

.\gradlew.bat bootRun or ./gradlew bootRun

```

From there, the frontend can be opened at:

```text
http://localhost:8080/landingPage.html
```

One flow can be:

1. Create an account.
2. Create a household or join one with an invite code.
3. Add a plant and pair its device ID.
4. Connect the pico to a power source. Make sure it has been configured
5. View incoming readings and plant status in the dashboard.
6. Add household members


## Project status and notes

Hydra is currently designed for local deployment. The next step is deployment so the project can be accessed from the internet. 