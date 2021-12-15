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
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
@Transactional
class PortService(
    private val circuitBreakerFactory: Resilience4JCircuitBreakerFactory
) {
    companion object {
        private val log = LoggerFactory.getLogger(PortService::class.java)
    }

    protected var ports: MutableList<PortDto> = mutableListOf()

    @Value("\${apiServiceAddress}")
    private lateinit var portServiceAddress: String

    private val lock = Any()

    private lateinit var cb: CircuitBreaker
    private val client = RestTemplate()

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

    fun UpdateOnePort(id: Long) {
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
        ) ?: return
        if (response.statusCodeValue != 200) log.error(
            "Error in fetching data from Port Service. Status ${response.statusCodeValue}. Message:${response.body?.message}"
        ) else
            try {
                val index = ports.indexOfFirst { x -> x.id == id }
                ports[index] = response.body!!.data!!

            } catch (e: Exception) {
                log.error("Failed to parse Port info: ${e.message}")
            }
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
                ports.addAll(response.body!!.data!!.list)
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
        return ports.any { x -> x.id == id }
    }
}
