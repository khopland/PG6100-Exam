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
        val env: KDockerComposeContainer = KDockerComposeContainer("E2E", File("../docker-compose.yml"))
            .withExposedService(
                "discovery", 8500,
                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(300))
            )
            .withLogConsumer("auth") { print("[auth] " + it.utf8String) }
            .withLocalCompose(true)

        @BeforeAll
        @JvmStatic
        fun waitForServers() {

            Awaitility.await().atMost(240, TimeUnit.SECONDS)
                .pollDelay(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(10))
                .ignoreExceptions()
                .until {
                    given().baseUri("http://${env.getServiceHost("discovery", 8500)}")
                        .port(env.getServicePort("discovery", 8500))
                        .get("/v1/agent/services")
                        .then()
                        .body("size()", CoreMatchers.equalTo(5))
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

                true
            }
    }
}