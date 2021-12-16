package org.tsdes.port.service

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

class WeatherDto(
    var weather: String? = null
)

@Component
class WeatherService (
    private val portService: PortService,
    private val repository: PortRepository,
    private val rabbit: RabbitTemplate,
    private val topicExchange: TopicExchange
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
        rabbit.convertAndSend(topicExchange.name, "update", port.id)
    }

}