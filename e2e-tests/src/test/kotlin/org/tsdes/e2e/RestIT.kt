package org.tsdes.e2e

import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit

@Disabled
@Testcontainers
class RestIT {
    companion object {
        init {
            enableLoggingOfRequestAndResponseIfValidationFails()
            port = 80
        }

        class KDockerComposeContainer(id: String, path: File) :
            DockerComposeContainer<KDockerComposeContainer>(id, path)

        @Container
        @JvmField
        val env: KDockerComposeContainer = KDockerComposeContainer("eksamen", File("../docker-compose.yml"))
            .withExposedService(
                "discovery", 8500,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
            )
            .withLogConsumer("auth") { print("[auth] " + it.utf8String) }
            .withLogConsumer("port") { print("[port] " + it.utf8String) }
            .withLogConsumer("boat_0") { print("[boat_0] " + it.utf8String) }
            .withLogConsumer("boat_1") { print("[boat_1] " + it.utf8String) }
            .withLogConsumer("trip") { print("[trip] " + it.utf8String) }
            .withOptions("--compatibility")
            .withLocalCompose(true)

        @BeforeAll
        @JvmStatic
        fun waitForServers() {

            Awaitility.await().atMost(360, TimeUnit.SECONDS)
                .pollDelay(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {
                    given().baseUri("http://${env.getServiceHost("discovery", 8500)}")
                        .port(env.getServicePort("discovery", 8500))
                        .get("/v1/agent/services")
                        .then()
                        .statusCode(200)
                        // add the number of services here
                        .body("size()", CoreMatchers.equalTo(6))
                    true
                }
        }

        @AfterAll
        @JvmStatic
        fun afterTests() {
            env.stop()
        }
    }

    private fun createUser(id: String, password: String): String {
        val cookie = given().contentType(ContentType.JSON)
            .body("""{"userId": "$id","password": "$password"}""".trimIndent())
            .post("/api/auth/signUp")
            .then()
            .statusCode(201)
            .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
            .extract().cookie("SESSION")

        given().cookie("SESSION", cookie)
            .get("/api/auth/user")
            .then()
            .statusCode(200)
        return cookie
    }

    private fun loginUser(id: String, password: String): String {
        val cookie = given().contentType(ContentType.JSON)
            .body("""{"userId": "$id","password": "$password"}""".trimIndent())
            .post("/api/auth/login")
            .then()
            .statusCode(204)
            .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
            .extract().cookie("SESSION")

        given().cookie("SESSION", cookie)
            .get("/api/auth/user")
            .then()
            .statusCode(200)
        return cookie
    }

    @Test
    fun testCreateUser() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "testUser" + System.currentTimeMillis()
                val password = "123456"

                createUser(id, password)
                true
            }
    }

    @Test
    fun testGetPorts() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                given().get("/api/port")
                    .then()
                    .statusCode(200)
                    .body("data.list.size()", Matchers.equalTo(10))
                true
            }
    }

    @Test
    fun testGetBoats() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                given().get("/api/boat")
                    .then()
                    .statusCode(200)
                    .body("data.list.size()", Matchers.equalTo(10))
                true
            }
    }

    @Test
    fun testGetTrips() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "testUser" + System.currentTimeMillis()
                val password = "123456"

                createUser(id, password)

                given().get("/api/trips")
                    .then()
                    .statusCode(200)
                true
            }
    }

    @Test
    fun testGetTripsAdmin() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "admin"
                val password = "admin"
                val cookie = loginUser(id, password)

                given().cookie("SESSION", cookie)
                    .get("/api/trips")
                    .then()
                    .statusCode(200)
                    .body("data.list.size()", Matchers.greaterThan(0))
                given().cookie("SESSION", cookie)
                    .get("/api/trips/1")
                    .then()
                    .statusCode(200)
                true
            }
    }

    @Test
    fun testCreateATrip() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "testUser_" + System.currentTimeMillis()
                val password = "123456"

                val cookie = createUser(id, password)
                val tripId = given().cookie("SESSION", cookie).contentType(ContentType.JSON).body(
                    """
                     {
                        "boat": 1,
                        "departure": 1,
                        "destination": 2,
                        "passengers": 5,
                        "status": 0,
                        "userId": "$id"
                    }
                """.trimIndent()
                )
                    .post("/api/trips")
                    .then()
                    .statusCode(201).extract().header("Location").split('/').last()

                given().cookie("SESSION", cookie)
                    .get("/api/trips/$tripId")
                    .then()
                    .statusCode(200)

                given().cookie("SESSION", cookie).contentType(ContentType.JSON).body("""{"status": 1}""".trimIndent())
                    .patch("/api/trips/$tripId")
                    .then()
                    .statusCode(204)

                given().cookie("SESSION", cookie)
                    .delete("/api/trips/$tripId")
                    .then()
                    .statusCode(204)

                given().cookie("SESSION", cookie)
                    .get("/api/trips/$tripId")
                    .then()
                    .statusCode(404)
                true
            }
    }

    @Test
    fun testCreateABoat() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "admin"
                val password = "admin"

                val cookie = loginUser(id, password)

                given().cookie("SESSION", cookie)
                    .get("/api/boat")
                    .then()
                    .statusCode(200)
                    .body("data.list.size()", Matchers.greaterThan(0))

                val boatId = given().cookie("SESSION", cookie).contentType(ContentType.JSON).body(
                    """{"name": "test", "builder": "colorLine", "numberOfCrew": 2,"maxPassengers":10,"minPassengers":2}""".trimIndent()
                )
                    .post("/api/boat")
                    .then()
                    .statusCode(201).extract().header("Location").split('/').last()

                val boatBody = given().cookie("SESSION", cookie)
                    .get("/api/boat/$boatId")
                    .then()
                    .statusCode(200).extract().body()

                given().cookie("SESSION", cookie).contentType(ContentType.JSON).body(
                    """{"id":$boatId,"name": "test2", "builder": "colorLine", "numberOfCrew": 2,"maxPassengers":10,"minPassengers":2}""".trimIndent()
                )
                    .put("/api/boat/$boatId")
                    .then()
                    .statusCode(204)

                given().cookie("SESSION", cookie)
                    .get("/api/boat/$boatId")
                    .then()
                    .statusCode(200).body(Matchers.not(boatBody))

                true
            }
    }

    @Test
    fun testCreateAPort() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "admin"
                val password = "admin"

                val cookie = loginUser(id, password)

                val portId = given().cookie("SESSION", cookie).contentType(ContentType.JSON).body(
                    """{"name": "test","weather": "foggy"}""".trimIndent()
                )
                    .post("/api/port")
                    .then()
                    .statusCode(201).extract().header("Location").split('/').last()

                given().cookie("SESSION", cookie)
                    .get("/api/port/$portId")
                    .then()
                    .statusCode(200)

                given().cookie("SESSION", cookie).contentType(ContentType.JSON).body(
                    """{"weather": "cold"}""".trimIndent()
                )
                    .patch("/api/port/$portId")
                    .then()
                    .statusCode(204)
                true
            }
    }


}