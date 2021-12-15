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
import org.tsdes.advanced.rest.dto.PageDto
import org.tsdes.advanced.rest.dto.WrappedResponse
import org.tsdes.trip.db.TripRepository
import org.tsdes.trip.service.TripService
import wiremock.com.fasterxml.jackson.databind.ObjectMapper
import javax.annotation.PostConstruct

@ActiveProfiles("RestAPITest", "test")
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

        @BeforeAll
        @JvmStatic
        fun initClass() {
            wiremockServer =
                WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort().notifier(ConsoleNotifier(true)))
            wiremockServer.start()


            val boatDto = WrappedResponse(code = 200, data = PageDto(FakeData.getBoatDTO(), null)).validated()
            val boatJson = ObjectMapper().writeValueAsString(boatDto)

            val portDto = WrappedResponse(code = 200, data = PageDto(FakeData.getPortDTO(), null)).validated()
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
                TestPropertyValues.of("apiServiceAddress: localhost:${wiremockServer.port()}")
                    .applyTo(configurableApplicationContext.environment)
            }
        }
    }

    @Test
    fun testAccessControl() {
        val id = 0

        given().get("/$id").then().statusCode(401)
        given().post("/$id").then().statusCode(401)
        given().patch("/$id").then().statusCode(401)

        given().auth().basic("bar", "123")
            .get("/$id").then().statusCode(404)
    }

    @Test
    fun testGetTrip() {
        val id = "foo"
        val trip = tripService.createTrip(id, 0, 1, 0,5)
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
              "boat": 0,
              "departure": 0,
              "destination": 1,
              "passengers": 5,
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
              "boat": 0,
              "departure": 0,
              "destination": 1,
              "passengers": 5,
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

}