package org.tsdes.boat

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
    private val service: BoatService
) {

    @LocalServerPort
    protected var port = 0

    @PostConstruct
    fun init() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/boat"
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
        given().auth().basic("extra", "extra")
            .put("/$id")
            .then()
            .statusCode(403)
        given()
            .get("/")
            .then()
            .statusCode(200)
    }

    @Test
    fun testGetBoat() {
        val id = service.registerNewBoat("test", "batty", 2).id

        given()
            .get("/$id")
            .then()
            .statusCode(200)
    }

    @Test
    fun testCreateBoat() {
        given().auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body("""{"name": "test", "builder": "colorLine", "numberOfCrew": 2}""".trimIndent())
            .post("/")
            .then()
            .statusCode(201)
    }

    @Test
    fun testUpdateBoat() {
        val id: String = given().auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body("""{"name": "test", "builder": "colorLine", "numberOfCrew": 2}""".trimIndent())
            .post("/")
            .then()
            .statusCode(201).extract().header("location").split('/').last()

        val firstBody = given()
            .get("/$id")
            .then()
            .statusCode(200).extract().body()

        given().auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body("""{"name": "test", "builder": "colorLine", "numberOfCrew": 2}""".trimIndent())
            .put("/$id")
            .then()
            .statusCode(400)

        given().auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body("""{"id": $id, "name": "test", "builder": "colorLine", "numberOfCrew": 2}""".trimIndent())
            .put("/$id")
            .then()
            .statusCode(204)

        val secondBody = given()
            .get("/$id")
            .then()
            .statusCode(200).extract().body()

        assert(firstBody != secondBody)
    }

    @Test
    fun testPaging() {
        service.registerNewBoat("test_", "batty", 2)
        given()
            .get("/")
            .then()
            .statusCode(200)
            .body("data.list.size()", Matchers.greaterThanOrEqualTo(0))
    }
}