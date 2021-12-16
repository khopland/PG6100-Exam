package org.tsdes.trip

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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
import org.tsdes.advanced.rest.dto.PageDto
import org.tsdes.advanced.rest.dto.WrappedResponse
import org.tsdes.dto.Status
import org.tsdes.trip.db.TripRepository
import org.tsdes.trip.service.TripService
import wiremock.com.fasterxml.jackson.databind.ObjectMapper
import javax.annotation.PostConstruct

@ActiveProfiles("RestAPITest", "test")
@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(RestAPITest.Companion.Initializer::class)])
internal class RestAPITest @Autowired constructor(
    private val tripService: TripService, private val tripRepository: TripRepository
) {

    @LocalServerPort
    protected var port = 0

    @PostConstruct
    fun init() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/api/trips"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @BeforeEach
    fun initTest() = tripRepository.deleteAll()

    companion object {
        private lateinit var wiremockServer: WireMockServer

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        @Container
        @JvmField
        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)!!

        @BeforeAll
        @JvmStatic
        fun initClass() {
            wiremockServer =
                WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().notifier(ConsoleNotifier(true)))
            wiremockServer.start()


            val boatDto =
                WrappedResponse(code = 200, data = PageDto(FakeData.getBoatDTO().values.toList(), null)).validated()
            val boatJson = ObjectMapper().writeValueAsString(boatDto)

            val portDto =
                WrappedResponse(code = 200, data = PageDto(FakeData.getPortDTO().values.toList(), null)).validated()
            val portJson = ObjectMapper().writeValueAsString(portDto)

            wiremockServer.stubFor(
                WireMock.get(WireMock.urlMatching("/api/boat/*")).willReturn(
                    WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody(boatJson)
                )
            )
            wiremockServer.stubFor(
                WireMock.get(WireMock.urlMatching("/api/port/*")).willReturn(
                    WireMock.aResponse().withStatus(200).withHeader("Content-Type", "application/json; charset=utf-8")
                        .withBody(portJson)
                )
            )
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            wiremockServer.stop()
        }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of(
                    "apiServiceAddress: localhost:${wiremockServer.port()}",
                    "spring.rabbitmq.host=" + rabbitMQ.containerIpAddress,
                    "spring.rabbitmq.port=" + rabbitMQ.getMappedPort(5672)
                ).applyTo(configurableApplicationContext.environment)
            }
        }
    }

    @Test
    fun testAccessControl() {
        val id = 0

        given().get("/$id").then().statusCode(401)
        given().post("/$id").then().statusCode(401)
        given().patch("/$id").then().statusCode(401)
        given().delete("/$id").then().statusCode(401)

        given().auth().basic("bar", "123")
            .get("/$id").then().statusCode(404)
    }

    @Test
    fun testGetTrip() {
        val id = "foo"
        val trip = tripService.createTrip(id, 1, 2, 1, 5, Status.BOOKED)
        assert(trip != null)
        given().auth().basic(id, "123")
            .get("/${trip!!.id}").then().statusCode(200)
    }

    @Test
    fun testCreateTrip() {
        val id = "foo"

        val tripId = given().auth().basic(id, "123").contentType(ContentType.JSON).body(
            """
            {
              "boat": 1,
              "departure": 2,
              "destination": 1,
              "passengers": 5,
              "status": 0,
              "userId": "$id"
            }
        """.trimIndent()
        ).post("/").then().statusCode(201)
            .extract().header("location").split("/").last()
        assertTrue(tripRepository.existsById(tripId.toLong()))
    }

    @Test
    fun testFailingCreateTrip() {
        val id = "foo"

        given().auth().basic(id, "123").contentType(ContentType.JSON).body(
            """
            {
              "boat": 1,
              "departure": 2,
              "destination": 1,
              "passengers": 5,
              "status": 0,
              "userId": "$id+sss"
            } 
        """.trimIndent()
        ).post("/").then().statusCode(400)
        assertTrue(tripRepository.findByUserId(id).isEmpty())
    }

    @Test
    fun testGetAllYourTrips() {
        val id = "foo"
        testCreateTrip()

        given().auth().basic(id, "123")
            .get("/").then().statusCode(200)
            .body("data.list.size()", Matchers.greaterThan(0))
    }

    @Test
    fun testDeleteYourTrips() {
        val id = "foo"

        val tripId = given().auth().basic(id, "123").contentType(ContentType.JSON).body(
            """
            {
              "boat": 1,
              "departure": 2,
              "destination": 1,
              "passengers": 5,
              "status": 0,
              "userId": "$id"
            }
        """.trimIndent()
        ).post("/").then().statusCode(201)
            .extract().header("location").split("/").last()
        assertTrue(tripRepository.existsById(tripId.toLong()))

        given().auth().basic(id, "123").delete("/$tripId").then().statusCode(204)
        assertFalse(tripRepository.existsById(tripId.toLong()))

    }

    @Test
    fun testChangeStatusYourTrips() {
        val id = "foo"

        val tripId = given().auth().basic(id, "123").contentType(ContentType.JSON).body(
            """
            {
              "boat": 1,
              "departure": 2,
              "destination": 1,
              "passengers": 5,
              "status": 0,
              "userId": "$id"
            }
        """.trimIndent()
        ).post("/").then().statusCode(201)
            .extract().header("location").split("/").last()
        assertTrue(tripRepository.existsById(tripId.toLong()))

        given().auth().basic(id, "123").contentType(ContentType.JSON).body("""{"status": 1}""".trimIndent())
            .patch("/$tripId").then().statusCode(204)
        assertTrue(tripRepository.findById(tripId.toLong()).get().status ==Status.ONGOING)

    }

}