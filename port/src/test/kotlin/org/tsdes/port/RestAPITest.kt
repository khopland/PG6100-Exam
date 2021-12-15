package org.tsdes.port

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.annotation.PostConstruct

@ActiveProfiles("RestAPITest", "test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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