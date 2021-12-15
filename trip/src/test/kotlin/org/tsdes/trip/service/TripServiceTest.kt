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
import org.tsdes.trip.FakeData
import org.tsdes.trip.db.TripRepository


@Profile("UserServiceTest")
@Primary
@Service
class FakeBoatService : BoatService(Resilience4JCircuitBreakerFactory()) {
    override fun fetchData() {
        val dto = FakeData.getBoatDTO()
        super.boats = dto.toMutableList()
    }
}

@Profile("UserServiceTest")
@Primary
@Service
class FakePortService : PortService(Resilience4JCircuitBreakerFactory()) {
    override fun fetchData() {
        val dto = FakeData.getPortDTO()
        super.ports = dto.toMutableList()
    }
}


@ActiveProfiles("UserServiceTest", "test")
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
        assertNull(tripService.createTrip("foo", 11, 1, 0,5,"Booked"))
        assertNull(tripService.createTrip("foo", 0, 1, 11,5,"Booked"))
        assertNull(tripService.createTrip("foo", 0, 1, 1,11,"Booked"))
        assertNotNull(tripService.createTrip("foo", 0, 1, 0,5,"Booked"))
        assertTrue(tripRepository.count() == 1L)
    }

    @Test
    fun testFailedCreateTrip() {
        assertNull(tripService.createTrip("foo", 11, 1, 0,5,"Booked"))
        assertNull(tripService.createTrip("foo", 0, 1, 11,5,"Booked"))
        assertTrue(tripRepository.count() == 0L)
    }
    @Test
    fun testPage() {
        val n = 5
        for (i in 0 until n)
            tripService.createTrip("foo", 0, 1, 0,5,"Booked")
        val page = tripService.getNextPage("foo",n)
        assertEquals(n, page.size)
        for (i in 0 until n - 1)
            assertTrue(page[i].id >= page[i + 1].id)
    }
    @Test
    fun testDelete() {
        val trip = tripService.createTrip("foo", 0, 1, 0,5,"Booked")
        assertEquals(tripRepository.count(), 1)
        tripService.deleteTrip(trip!!.id,trip.userId)
        assertEquals(tripRepository.count(), 0)

    }
}