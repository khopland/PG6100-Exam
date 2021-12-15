package org.tsdes.port

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InitialDataLoader(
    private val repo: PortRepository,
    private val service: PortService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createDefaultData()
    }

    private fun createDefaultData() {
        if (repo.count() == 0L) {
            service.registerNewPort("Yamaha_Motor_Corporation", "Batty")
            service.registerNewPort("Oslo", "Snow")
            service.registerNewPort("Bergen", "Rain")
            service.registerNewPort("Trondheim", "Snow")
            service.registerNewPort("Ålesund", "Rain")
            service.registerNewPort("Haugesund", "Rain")
            service.registerNewPort("Drammen", "Sun")
            service.registerNewPort("Alvik", "Rain")
            service.registerNewPort("Brevik", "Rain")
            service.registerNewPort("Bodo", "Snow")
            service.registerNewPort("Fredrikstad", "Sun")
            service.registerNewPort("Egersund", "Rain")
        }
    }
}