package org.tsdes.trip

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.tsdes.advanced.rest.dto.PageDto
import org.tsdes.advanced.rest.dto.RestResponseFactory
import org.tsdes.advanced.rest.dto.WrappedResponse
import org.tsdes.dto.TripDto
import org.tsdes.trip.service.TripService
import java.net.URI
import java.util.concurrent.TimeUnit

@Api(value = "/api/trips", description = "Operations on card collections owned by users")
@RequestMapping(
    "/api/trips",
    produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
class RestAPI(
    private val tripService: TripService
) {

    @ApiOperation("get a trip by an id")
    @GetMapping("/{tripId}")
    fun getTrip(
        @PathVariable("tripId") tripId: Long
    ): ResponseEntity<WrappedResponse<TripDto>> {
        val username = getUser() ?: return RestResponseFactory.noPayload(401)

        val trip = tripService.getTripById(tripId) ?: return RestResponseFactory.notFound("no trip with id = $tripId")
        if (trip.userId != username) return RestResponseFactory.noPayload(403)
        return RestResponseFactory.payload(200, trip.toDto())
    }

    @ApiOperation("get all trip for the user")
    @GetMapping
    fun getAllTripsByUser(
        @RequestParam("keysetId", required = false)
        keysetId: Long?
    ): ResponseEntity<WrappedResponse<PageDto<TripDto>>> {
        val username = getUser() ?: return RestResponseFactory.noPayload(401)

        val amount = 10
        val page = PageDto<TripDto>().apply {
            list = tripService.getNextPage(username, amount, keysetId).map { it.toDto() }
        }
        if (page.list.size == amount)
            page.next = "/api/trips?keysetId=${page.list.last().id}"
        return ResponseEntity
            .status(200)
            .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
            .body(WrappedResponse(200, page).validated())

    }

    @ApiOperation("create a Trip")
    @PostMapping
    fun createTrip(
        @RequestBody dto: TripDto
    ): ResponseEntity<WrappedResponse<Void>> {
        val username = getUser() ?: return RestResponseFactory.noPayload(401)
        if (dto.userId != username)
            return RestResponseFactory.userFailure("userId in body is not your Id")

        val trip = tripService.createTrip(dto)
            ?: return RestResponseFactory.userFailure("cant find Boat or Port, or not right amount of passengers")
        return RestResponseFactory.created(URI.create("api/trip/${trip.id}"))

    }

    private fun getUser(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth.principal !is UserDetails) return null
        return (auth.principal as UserDetails).username
    }
}



