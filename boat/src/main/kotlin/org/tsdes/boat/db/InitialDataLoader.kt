package org.tsdes.boat.db

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InitialDataLoader(
    private val repo: BoatRepository,
    private val service: BoatService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createDefaultData()
    }

    private fun createDefaultData() {
        if (repo.count() == 0L) {
            service.registerNewBoat("Yamaha_Motor_Corporation", "Batty", 3, 10, 2)
            service.registerNewBoat("Yamaha_Motor_Corporation", "Speedy", 20, 100, 20)
            service.registerNewBoat("Benetti", "Decisive", 2, 6, 1)
            service.registerNewBoat("Princess", "Skybird", 5, 20, 4)
            service.registerNewBoat("Princess", "Spice", 10, 100, 10)
            service.registerNewBoat("Princess", "Royal_Eagle", 3, 6, 3)
            service.registerNewBoat("Codecasa", "Second_Step", 1, 3, 1)
            service.registerNewBoat("Codecasa", "Penny", 50, 1000, 400)
            service.registerNewBoat("Codecasa", "Sundown", 2, 4, 2)
            service.registerNewBoat("Lazzara", "Knocker", 20, 100, 10)
            service.registerNewBoat("Lazzara", "Champagne", 3, 15, 10)
        }
    }
}