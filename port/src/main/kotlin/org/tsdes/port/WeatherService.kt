package org.tsdes.port

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WeatherService @Autowired constructor(
    private val portService: PortService,
    private val repository: PortRepository,
) {
    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(WeatherService::class.java)
    }

    val whetherList = listOf("Sun", "Snow", "Rain", "Foggy", "Cloudy", "Storm")

    @Scheduled(fixedDelay = 10_000L, initialDelay = 10_000L, zone = "Europe/Oslo")
    fun updateWeather() {
        val port = repository.findAll().toList().random()
        val whether = whetherList.random()
        portService.updateWhether(port.id, whether)
        log.info("port ${port.id} updated Weather to $whether")
    }

}