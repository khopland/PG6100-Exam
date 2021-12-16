@file:Suppress("DEPRECATION")

package org.tsdes.trip.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import org.tsdes.dto.Status
import org.tsdes.trip.FakeData
import org.tsdes.trip.db.TripRepository


@Profile("TripServiceTest")
@Primary
@Service
class FakeBoatService : BoatService(RestTemplate(), Resilience4JCircuitBreakerFactory()) {
    override fun fetchData() {
        super.boats = FakeData.getBoatDTO()
    }
}

@Profile("TripServiceTest")
@Primary
@Service
class FakePortService : PortService(RestTemplate(), Resilience4JCircuitBreakerFactory()) {
    override fun fetchData() {
        super.ports = FakeData.getPortDTO()
    }
}


@ActiveProfiles("TripServiceTest", "test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class TripServiceTest @Autowired constructor(
    private val tripService: TripService,
    private val tripRepository: TripRepository
) {

    @BeforeEach
    fun initTest() = tripRepository.deleteAll()


    @Test
    fun testCreateTrip() {
        assertNotNull(tripService.createTrip("foo", 1, 2, 1, 5, Status.BOOKED))
        assertTrue(tripRepository.count() == 1L)
    }

    @Test
    fun testFailedCreateTrip() {
        assertNull(tripService.createTrip("foo", 11, 1, 0, 5, Status.BOOKED))
        assertNull(tripService.createTrip("foo", 0, 1, 11, 5, Status.BOOKED))
        assertNull(tripService.createTrip("foo", 0, 1, 1, 11, Status.BOOKED))
        assertTrue(tripRepository.count() == 0L)
    }

    @Test
    fun testPage() {
        val n = 5
        for (i in 0 until n)
            assertNotNull(tripService.createTrip("foo", 1, 2, 1, 5, Status.BOOKED))
        val page = tripService.getNextPage( n)
        assertEquals(n, page.size)
        for (i in 0 until n - 1)
            assertTrue(page[i].id <= page[i + 1].id)
    }

    @Test
    fun testDelete() {
        val trip = tripService.createTrip("foo", 1, 2, 1, 5, Status.BOOKED)
        assertEquals(tripRepository.count(), 1)
        tripService.deleteTrip(trip!!.id, trip.userId)
        assertEquals(tripRepository.count(), 0)

    }

    @Test
    fun testChangeWeather() {
        val trip = tripService.createTrip("foo", 1, 2, 1, 5, Status.ONGOING)
        val trip2 = tripService.createTrip("foo", 1, 2, 1, 5, Status.BOOKED)
        assertEquals(tripRepository.count(), 2)
        tripService.updateOnWeather(2)
        assertEquals(tripRepository.findById(trip!!.id).get().status, Status.CANSCELLD)
        assertNotEquals(tripRepository.findById(trip2!!.id).get().status, Status.CANSCELLD)
    }
    @Test
    fun testChangeStatus() {
        val trip = tripService.createTrip("foo", 1, 2, 1, 5, Status.BOOKED)
        assertEquals(tripRepository.count(), 1)
        assertTrue(tripService.updateStatus(trip!!.id,trip.userId,Status.CANSCELLD))
        assertEquals(tripRepository.findById(trip.id).get().status, Status.CANSCELLD)
    }
    @Test
    fun testFailChangeStatus() {
        val trip = tripService.createTrip("foo", 1, 2, 1, 5, Status.BOOKED)
        assertEquals(tripRepository.count(), 1)
        assertFalse(tripService.updateStatus(trip!!.id,trip.userId+"asddd",Status.CANSCELLD))
        assertNotEquals(tripRepository.findById(trip.id).get().status, Status.CANSCELLD)
    }
}