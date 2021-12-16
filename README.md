> [PG6100 - Enterpriseprogrammering 2](https://old.kristiania.no/emnebeskrivelse-2-2/?kode=PG6102&arstall=2021&terminkode=H%C3%98ST&v=1) | [Oppgavetekst](./docs/2021-12_exam.pdf) | [Course Repository](https://github.com/arcuri82/testing_security_development_enterprise_systems)

# pg6100-exam

## Modules

### Gateway

* Spring Cloud Gateway as a reverse-proxy that routes HTTP-requests to the other services based on path.
* Load Balance between multiple instances of Boat Service

### Auth

* Spring Security Authentication Service.
* Uses Redis for distributed session based authentication across the services.
* Sends AMQP messages on created users.

### Port

* Entity Service for Read operations on Trips.
* Admin can create new ports and Update Whether on a port.
* Whether changes on a random port every 10s.
* Sends AMQP messages on create and update to a Topic exchange.

### Boat

* Entity Service for Read operations on Boats.
* Admin can create, update and delete Boats.
* Sends AMQP messages on create and update to a Topic exchange.

### Trip

* Entity Service for CRUD operations on Trips.
* a user can read all trips, but only a user can delete, update status and create on the specific user.
* gets data from boat and Port with circuit breakers and updates the data on AMQP messages from boat and port service.
* Sends AMQP messages on create to a fanout exchange.

### Frontend

* React frontend built with vite for GUI.
* Supports signup, login and logout.
* supports creating a trip with listing of boats and ports to travel to and set the Status of the trip.
* supports listing all trips based on ports or boats from a dropdown.
* supports listing all trips the user is registered on.
* Builds the frontend in docker build container and send the build file to a second container to run in production.

### E2E-tests

* Uses Testcontainers with Docker-Compose, Availability and RestAssured to perform End-to-End tests on the service.
* disabled by default for faster build time

### rest-dto & rest-exception

* Its 2 library's from the Course to handle DTOs and Exceptions

## Commands:

* `mvn clean install -DskipTests`
    * Install all maven and npm dependency and builds the services without running tests.
* `mvn clean verify`
    * to run all tests except E2E.
* `docker compose build`
    * Build all the docker images
* `docker compose up --build`
    * Starts all docker images
* `docker compose up -d --build && npm run dev --prefix frontend`
    * Starts all docker images and runs frontend locally on http://localhost:3000

### Ports:

| Service: | Port: |
|----------| ---   |
| Gateway  | 80    |
| Frontend | 3000  |
| Auth     | 8080  |
| Port     | 8080  |
| Boat     | 8080  |
| Trip     | 8080  |
|          |       |
| Consul   | 8500  |
| Redis    | 6379  |
| RabbitMQ | 5672  |

## Notes

* All requirements, R1 through R5 and T1 through T4, are complete.
* Admin login for the service uses `admin:admin`.
