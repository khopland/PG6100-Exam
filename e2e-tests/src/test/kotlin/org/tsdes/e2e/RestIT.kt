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
        val env: KDockerComposeContainer = KDockerComposeContainer("exam", File("../docker-compose.yml"))
            .withExposedService(
                "discovery", 8500,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
            )
            .withLogConsumer("auth") { print("[auth] " + it.utf8String) }
            .withLogConsumer("port") { print("[port] " + it.utf8String) }
            .withLogConsumer("boat_0") { print("[boat_0] " + it.utf8String) }
            .withLogConsumer("boat_1") { print("[boat_1] " + it.utf8String) }
            .withLogConsumer("trip") { print("[trip] " + it.utf8String) }

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

    @Test
    fun testCreateUser() {
        Awaitility.await().atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(10))
            .ignoreExceptions()
            .until {
                val id = "foo_testCreateUser_" + System.currentTimeMillis()
                val password = "123456"

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
                val id = "foo_testCreateUser_" + System.currentTimeMillis()
                val password = "123456"

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

                given().get("/api/trips")
                    .then()
                    .statusCode(401)
                given().cookie("SESSION", cookie)
                    .get("/api/trips")
                    .then()
                    .statusCode(200)
                    .body("data.list.size()", Matchers.equalTo(0))

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

                given().cookie("SESSION", cookie)
                    .get("/api/trips")
                    .then()
                    .statusCode(200)
                    .body("data.list.size()", Matchers.equalTo(1))
                given().cookie("SESSION", cookie)
                    .get("/api/trips/1")
                    .then()
                    .statusCode(200)
                true
            }
    }


}