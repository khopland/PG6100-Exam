package org.tsdes.port

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.tsdes.port.service.PortService
import javax.annotation.PostConstruct

@ActiveProfiles("RestAPITest", "test")
@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(RestAPITest.Companion.Initializer::class)])
internal class RestAPITest @Autowired constructor(
    private val service: PortService
) {

    @LocalServerPort
    protected var port = 0

    @PostConstruct
    fun init() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    companion object {
        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        @Container
        @JvmField
        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)!!

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) =
                TestPropertyValues.of(
                    "spring.rabbitmq.host=" + rabbitMQ.containerIpAddress,
                    "spring.rabbitmq.port=" + rabbitMQ.getMappedPort(5672)
                ).applyTo(configurableApplicationContext.environment)
        }
    }

    @Test
    fun testAccessControl() {
        val id = "foo"

        given().post("/$id").then().statusCode(401)
        given().put("/$id").then().statusCode(401)
        given().patch("/$id").then().statusCode(401)

        given().auth().basic("user", "user")
            .post("/$id")
            .then()
            .statusCode(403)
        given()
            .get("/")
            .then()
            .statusCode(200)
    }

    @Test
    fun testGetPort() {
        val id = service.registerNewPort("test", "foggy").id

        given()
            .get("/$id")
            .then()
            .statusCode(200)
    }

    @Test
    fun testCreatePort() {
        given().auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body("""{"name": "test","weather": "foggy"}""".trimIndent())
            .post("/")
            .then()
            .statusCode(201)
    }

    @Test
    fun testPaging() {
        service.registerNewPort("test", "foggy")
        given()
            .get("/")
            .then()
            .statusCode(200).body("data.list.size()", Matchers.greaterThan(0))
    }


}