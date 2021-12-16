package org.tsdes.trip.db

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InitialDataLoader(
    private val repo: TripRepository,
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createDefaultData()
    }

    private fun createDefaultData() {
        if (repo.count() == 0L) {
            repo.save(Trip(0, "admin", 1L, 1L, 2L, 5, "Booked"))
        }
    }
}