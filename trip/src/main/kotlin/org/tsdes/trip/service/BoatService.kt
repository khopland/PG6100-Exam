package org.tsdes.trip.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.tsdes.advanced.rest.dto.PageDto
import org.tsdes.advanced.rest.dto.WrappedResponse
import org.tsdes.dto.BoatDto
import java.net.URI
import java.time.LocalDateTime
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
@Transactional
class BoatService(
    private val client: RestTemplate,
    private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory
) {
    companion object {
        private val log = LoggerFactory.getLogger(BoatService::class.java)
    }

    protected var boats: HashMap<Long, BoatDto> = HashMap()

    @Value("\${apiServiceAddress}")
    private lateinit var boatServiceAddress: String

    private val lock = Any()
    private var lastFetch: LocalDateTime = LocalDateTime.MIN

    private lateinit var cb: CircuitBreaker

    @PostConstruct
    fun init() {
        cb = circuitBreakerFactory.create("circuitBreakerToBoats")

        synchronized(lock) {
            if (boats.isNotEmpty()) {
                return
            }
            fetchData()
        }
    }

    fun updateOneBoat(id: Long) {
        val boat = getABoat(id) ?: return
        boats[id] = boat
    }

    fun getNewOneBoat(id: Long) {
        val boat = getABoat(id) ?: return
        boats.putIfAbsent(id, boat)
    }


    private fun getABoat(id: Long): BoatDto? {
        val uri = UriComponentsBuilder.fromUriString("http://${boatServiceAddress.trim()}/api/boat/$id").build().toUri()
        val response = cb.run({
            client.exchange(uri,
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<WrappedResponse<BoatDto>>() {})
        }, {
            log.error("Failed to fetch data from Boat Service: ${it.message}")
            null
        }) ?: return null
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from Boat Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else try {
            return response.body!!.data!!
        } catch (e: Exception) {
            log.error("Failed to parse Boat info: ${e.message}")
        }
        return null
    }

    protected fun fetchData() {
        val uri = UriComponentsBuilder.fromUriString("http://${boatServiceAddress.trim()}/api/boat").build().toUri()
        fetchData(uri)
    }


    protected fun fetchData(uri: URI) {
        val response = cb.run({
            client.exchange(uri,
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<WrappedResponse<PageDto<BoatDto>>>() {})
        }, {
            log.error("Failed to fetch data from Boat Service: ${it.message}")
            null
        }) ?: return
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from Boat Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else try {
            if (response.body!!.data!!.next != null) fetchData(
                UriComponentsBuilder.fromUriString("http://${boatServiceAddress.trim()}${response.body!!.data!!.next!!}")
                    .build().toUri()
            )
            response.body!!.data!!.list.forEach { boats[it.id!!] = it }
            log.info("boats count ${boats.count()}")
            lastFetch = LocalDateTime.now()
        } catch (e: Exception) {
            log.error("Failed to parse Boat info: ${e.message}")
        }
    }

    private fun verifyBoats() {
        if (boats.isEmpty()) {
            fetchData()

            if (boats.isEmpty()) {
                throw IllegalStateException("No boat info")
            }
        }
    }

    fun boatExist(id: Long): Boolean {
        verifyBoats()
        return boats[id] != null
    }

    fun validateBoat(id: Long, passenger: Int): Boolean {
        if (lastFetch.plusMinutes(1).isBefore(LocalDateTime.now()))
            synchronized(lock) { fetchData() }
        if (!boatExist(id)) return false
        val boat = boats[id]!!
        return passenger >= boat.minPassengers && passenger <= boat.maxPassengers
    }
}