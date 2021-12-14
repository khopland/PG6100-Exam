package org.tsdes.trip.service

import org.springframework.stereotype.Service
import org.tsdes.dto.TripDto
import org.tsdes.trip.db.Trip
import org.tsdes.trip.db.TripRepository
import org.tsdes.trip.dto.FullTripDto
import javax.transaction.Transactional

@Service
@Transactional
class TripService(
    private val tripRepository: TripRepository,
    private val boatService: BoatService,
    private val portService: PortService
) {
    fun createTrip(tripDto: TripDto): Trip =
        createTrip(tripDto.userId, tripDto.departure!!, tripDto.destination!!, tripDto.boat!!)

    fun createTrip(userId: String, departure: Long, destination: Long, boat: Long): Trip {
        if (
            !boatService.boatExist(boat) ||
            !portService.portExist(departure) ||
            !portService.portExist(destination)
        ) throw IllegalStateException("Card service is not initialized")
        return tripRepository.save(Trip(0, userId, departure, destination, boat))
    }

    fun getTripsByUserId(userId: String): List<Trip> {
        return tripRepository.findByUserId(userId)
    }

    fun getTripById(id: Long): Trip? =
        tripRepository.findById(id).orElse(null)


    fun GetFullTripDtoById(id: Long): FullTripDto = tripRepository.findById(id).get().let {
        FullTripDto(
            id,
            it.userId,
            portService.getPortById(it.departure!!),
            portService.getPortById(it.destination!!),
            boatService.getBoatById(it.boat!!)
        )
    }


}