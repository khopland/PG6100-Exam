package org.tsdes.trip.service

import org.springframework.stereotype.Service
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
    fun createTrip(tripDto: TripDto): Trip? =
        createTrip(tripDto.userId, tripDto.departure!!, tripDto.destination!!, tripDto.boat!!, tripDto.passengers)

    fun createTrip(userId: String, departure: Long, destination: Long, boat: Long, passenger: Int): Trip? {
        if (
            boatService.validateBoat(boat, passenger) &&
            portService.portExist(departure) &&
            portService.portExist(destination)
        )
            return tripRepository.save(Trip(0, userId, departure, destination, boat, passenger))

        return null
    }

    fun getNextPage(userId: String, size: Int, keysetId: Long? = null): List<Trip> = when {
        size < 1 || size > 1000 -> throw IllegalArgumentException("Invalid size value: $size")

        else -> when (keysetId) {
            null -> em.createQuery(
                "select t from Trip t where t.userId =?1 order by t.id DESC",
                Trip::class.java
            ).setParameter(1, userId).apply { maxResults = size }.resultList
            else -> em.createQuery(
                "select p from Trip p where p.id<?1 and p.userId =?2 order by p.id DESC",
                Trip::class.java
            ).let { it.setParameter(1, keysetId);it.setParameter(2, keysetId); it.setMaxResults(size) }.resultList
        }
    }

    fun getTripById(id: Long): Trip? =
        tripRepository.findById(id).orElse(null)

}