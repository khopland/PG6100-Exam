package org.tsdes.trip

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.tsdes.trip.db.Trip
import org.tsdes.trip.db.TripRepository

@Component
class InitialDataLoader(
    private val repo: TripRepository,
): CommandLineRunner {

    override fun run(vararg args: String?) {
        createDefaultData()
    }
    private fun createDefaultData() {
        if (repo.count() == 0L) {
            repo.save(Trip(0,"admin",1L,1L,2L,5,"Booked"))
        }
    }
}