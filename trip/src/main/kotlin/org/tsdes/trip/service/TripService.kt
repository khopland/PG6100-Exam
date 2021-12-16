package org.tsdes.trip.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.tsdes.dto.Status
import org.tsdes.dto.TripDto
import org.tsdes.trip.db.Trip
import org.tsdes.trip.db.TripRepository
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class TripService(
    private val tripRepository: TripRepository,
    private val boatService: BoatService,
    private val portService: PortService,
    private val em: EntityManager
) {
    private val log = LoggerFactory.getLogger(TripService::class.java)
    fun createTrip(tripDto: TripDto): Trip? =
        createTrip(
            tripDto.userId,
            tripDto.departure!!,
            tripDto.destination!!,
            tripDto.boat!!,
            tripDto.passengers,
            tripDto.status
        )

    fun createTrip(
        userId: String,
        departure: Long,
        destination: Long,
        boat: Long,
        passenger: Int,
        status: Status
    ): Trip? {
        if (
            boatService.validateBoat(boat, passenger) &&
            portService.portExist(departure) &&
            portService.portExist(destination)
        )
            return tripRepository.save(Trip(0, userId, departure, destination, boat, passenger, status))

        log.info(
            "failed to create trip " +
                    "validateBoat ${boatService.validateBoat(boat, passenger)} " +
                    "departure ${portService.portExist(departure)} " +
                    "destination ${portService.portExist(destination)} "
        )
        return null
    }

    fun deleteTrip(id: Long, userId: String): Boolean {
        if (tripRepository.findById(id).orElseGet(null)?.userId != userId) return false
        tripRepository.deleteById(id)
        return true
    }


    fun getNextPage(size: Int, keysetId: Long? = null): List<Trip> = when {
        size < 1 || size > 1000 -> throw IllegalArgumentException("Invalid size value: $size")

        else -> when (keysetId) {
            null -> em.createQuery(
                "select t from Trip t order by t.id ASC",
                Trip::class.java
            ).apply { maxResults = size }.resultList
            else -> em.createQuery(
                "select p from Trip p where p.id>?1 order by p.id ASC",
                Trip::class.java
            ).let { it.setParameter(1, keysetId); it.setMaxResults(size) }.resultList
        }
    }

    fun getTripById(id: Long): Trip? =
        tripRepository.findById(id).orElse(null)

    fun updateOnWeather(portId: Long) {
        tripRepository.findByDestinationId(portId).filter { it.status == Status.ONGOING }
            .forEach { tripRepository.save(it.apply { this.status = Status.CANSCELLD }) }
    }

    fun updateStatus(tripId: Long, username: String, status: Status): Boolean {
        val trip = tripRepository.findById(tripId).orElseGet(null) ?: return false
        if (trip.userId != username) return false
        tripRepository.save(trip.apply { this.status = status })
        return true
    }


}