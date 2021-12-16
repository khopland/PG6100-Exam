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
import org.tsdes.dto.PortDto
import java.net.URI
import java.time.LocalDateTime
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
@Transactional
class PortService(
    private val client: RestTemplate,
    private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory
) {
    companion object {
        private val log = LoggerFactory.getLogger(PortService::class.java)
    }

    protected var ports: HashMap<Long, PortDto> = HashMap()

    @Value("\${apiServiceAddress}")
    private lateinit var portServiceAddress: String

    private val lock = Any()
    private var lastFetch: LocalDateTime = LocalDateTime.MIN


    private lateinit var cb: CircuitBreaker

    @PostConstruct
    fun init() {
        cb = circuitBreakerFactory.create("circuitBreakerToPorts")

        synchronized(lock) {
            if (ports.isNotEmpty()) {
                return
            }
            fetchData()
        }
    }

    fun updateOnePort(id: Long): Long? {
        val port = getAPort(id) ?: return null
        ports[id] = port
        if (port.weather.lowercase() == "storm")
            return port.id
        return null
    }

    fun getNewOnePort(id: Long) {
        val port = getAPort(id) ?: return
        ports.putIfAbsent(id, port)
    }


    private fun getAPort(id: Long): PortDto? {
        val uri = UriComponentsBuilder
            .fromUriString("http://${portServiceAddress.trim()}/api/port/$id")
            .build().toUri()
        val response = cb.run(
            {
                client.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<WrappedResponse<PortDto>>() {})
            },
            {
                log.error("Failed to fetch data from Port Service: ${it.message}")
                null
            }
        ) ?: return null
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from Port Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else
            try {
                return response.body!!.data!!
            } catch (e: Exception) {
                log.error("Failed to parse Port info: ${e.message}")
            }
        return null
    }

    protected fun fetchData() {

        val uri = UriComponentsBuilder
            .fromUriString("http://${portServiceAddress.trim()}/api/port")
            .build().toUri()
        fetchData(uri)
    }


    protected fun fetchData(uri: URI) {
        val response = cb.run(
            {
                client.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    object : ParameterizedTypeReference<WrappedResponse<PageDto<PortDto>>>() {})
            },
            {
                log.error("Failed to fetch data from Port Service: ${it.message}")
                null
            }
        ) ?: return
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from Port Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else
            try {
                if (response.body!!.data!!.next != null)
                    fetchData(
                        UriComponentsBuilder.fromUriString("http://${portServiceAddress.trim()}${response.body!!.data!!.next!!}")
                            .build().toUri()
                    )
                response.body!!.data!!.list.forEach { ports[it.id!!] = it }
                log.info("ports count ${ports.count()}")
                lastFetch = LocalDateTime.now()
            } catch (e: Exception) {
                log.error("Failed to parse Port collection info: ${e.message}")
            }
    }

    private fun verifyPorts() {
        if (ports.isEmpty()) {
            fetchData()

            if (ports.isEmpty()) {
                throw IllegalStateException("No boat info")
            }
        }
    }

    fun portExist(id: Long): Boolean {
        verifyPorts()
        if (lastFetch.plusMinutes(1).isBefore(LocalDateTime.now()))
            synchronized(lock) { fetchData() }
        return ports[id] != null
    }
}
