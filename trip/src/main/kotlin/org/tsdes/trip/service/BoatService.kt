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
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
@Transactional
class BoatService(
    private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory
) {
    companion object {
        private val log = LoggerFactory.getLogger(BoatService::class.java)
    }

    private var boats: MutableList<BoatDto> = mutableListOf()

    @Value("\${apiServiceAddress}")
    private lateinit var boatServiceAddress: String

    private val lock = Any()

    private lateinit var cb: CircuitBreaker
    private val client = RestTemplate()

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

    fun UpdateOneBoat(id: Long) {
        val uri = UriComponentsBuilder
            .fromUriString("http://${boatServiceAddress.trim()}/api/boat/$id")
            .build().toUri()
        val response = cb.run(
            {
                client.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<WrappedResponse<BoatDto>>() {})
            },
            {
                log.error("Failed to fetch data from Card Service: ${it.message}")
                null
            }
        ) ?: return
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from boat Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else
            try {
                val index = boats.indexOfFirst { x -> x.id == id }
                boats[index] = response.body!!.data!!

            } catch (e: Exception) {
                log.error("Failed to parse card collection info: ${e.message}")
            }
    }

    private fun fetchData() {

        val uri = UriComponentsBuilder
            .fromUriString("http://${boatServiceAddress.trim()}/api/boat")
            .build().toUri()
        fetchData(uri)
    }


    private fun fetchData(uri: URI) {
        val response = cb.run(
            {
                client.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<WrappedResponse<PageDto<BoatDto>>>() {})
            },
            {
                log.error("Failed to fetch data from Boat Service: ${it.message}")
                null
            }
        ) ?: return
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from Boat Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else
            try {
                if (response.body!!.data!!.next != null)
                    fetchData(URI(response.body!!.data!!.next!!))
                boats.addAll(response.body!!.data!!.list)
            } catch (e: Exception) {
                log.error("Failed to parse card collection info: ${e.message}")
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

    fun getBoatById(id: Long): BoatDto? {
        verifyBoats()
        return boats.firstOrNull { x -> x.id == id }
    }
    fun boatExist(id: Long): Boolean {
        verifyBoats()
        return boats.any { x -> x.id == id }
    }

    fun getBoatList(): List<BoatDto> {
        verifyBoats()
        return boats.toList()
    }


}