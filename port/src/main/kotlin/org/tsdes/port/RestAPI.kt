package org.tsdes.port

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.tsdes.advanced.rest.dto.PageDto
import org.tsdes.advanced.rest.dto.RestResponseFactory
import org.tsdes.advanced.rest.dto.WrappedResponse
import org.tsdes.dto.PortDto
import org.tsdes.port.db.toDto
import java.net.URI
import java.util.concurrent.TimeUnit

@Api(value = "api/port", description = "Endpoint for managing Trips")
@RestController
@RequestMapping(
    path = ["api/port"],
    produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
class RestAPI(
    private val portService: PortService
) {

    @GetMapping("/{portId}")
    fun getPort(
        @PathVariable("portId") id: String
    ): ResponseEntity<WrappedResponse<PortDto>> {
        val port = portService.getById(id.toLong()) ?: return RestResponseFactory.notFound("Port with $id not found")
        return RestResponseFactory.payload(200, port.toDto())
    }

    @PostMapping
    @ApiOperation("Create a new Port")
    fun createPort(
        @ApiParam("Name of New Port")
        @RequestBody dto: PortDto
    ): ResponseEntity<WrappedResponse<Void>> {
        val port = portService.registerNewPort(dto.name, dto.weather)
        // Return path to the created Trip
        return RestResponseFactory.created(URI.create("api/port/${port.id}"))
    }

    @ApiOperation("Return an page of ports")
    @GetMapping
    fun getAllPorts(
        @RequestParam("keysetId", required = false)
        keysetId: Long?
    ): ResponseEntity<WrappedResponse<PageDto<PortDto>>> {
        // Set amount if not supplied
        val amount = 10
        val page = PageDto<PortDto>().apply {
            list = (portService.getNextPage(amount, keysetId).map { it.toDto() })
        }
        if (page.list.size == amount)
            page.next = "/api/port?keysetId=${page.list.last().id}"
        return ResponseEntity
            .status(200)
            .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePublic())
            .body(WrappedResponse(200, page).validated())
    }

    @ApiOperation("updates Weather of a Port")
    @PatchMapping("/{id}")
    fun updateWeather(
        @PathVariable("id") id: Long,
        @RequestBody dto: WeatherDto
    ): ResponseEntity<WrappedResponse<Void>> {
        if (portService.updateWhether(id, dto.Weather!!))
            return RestResponseFactory.notFound("no port on this id $id")
        return RestResponseFactory.noPayload(204)
    }
}