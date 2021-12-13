> [PG6100 - Enterpriseprogrammering 2](https://old.kristiania.no/emnebeskrivelse-2-2/?kode=PG6102&arstall=2020&terminkode=H%C3%98ST) | [Oppgavetekst](./docs/PG6102_enterpriseprogramming2_exam_2020_fall.pdf) | [Course Repository](https://github.com/arcuri82/testing_security_development_enterprise_systems)
# pg6100-exam
## Moduless
### Gateway
* Spring Cloud Gateway as a reverse-proxy that routes HTTP-requests to the other services based on path.
### Auth
* Spring Security Authentication Service.
* Uses Redis for distributed session based authentication across the services.
* Sends AMQP messages on created users.
### Frontend
* React frontend buildt with vite for GUI.
* Supports signup and login.
* Builds the node project in docker container and send it to a second one.
### E2E-tests
* Uses Testcontainers with Docker-Compose, Awaitility and RestAssured to perform End-to-End tests on the service.
### rest-dto & rest-exception
* Its 2 librarys from the Course to handle DTOs and Exceptions


## Commands:
* `mvn clean install -DskipTests`
    * Install all maven and npm dependency and builds the services.
* `docker compose build`
    * Build all the docker images
* `docker compose up`
    * Starts all docker images
* `docker compose up -d && npm run dev --prefix frontend`
    * Starts all docker images and runs frontend localy on http://localhost:3000



### Ports:
| Service:  | Port: |
| ---       | ---   |
| Gateway   | 80    |
| Frontend  | 3000  |
| Auth      | 8080  |
|           |       |
| Consul    | 8500  |
| Redis     | 6379  |
| RabbitMQ  | 5672  |

## Notes
* All requirements, R1 through R5 and T1 through T4, are complete.
* Admin login for the service uses `admin:admin`.