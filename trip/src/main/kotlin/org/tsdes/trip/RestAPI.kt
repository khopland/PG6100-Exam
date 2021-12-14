package org.tsdes.trip

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.tsdes.advanced.rest.dto.RestResponseFactory
import org.tsdes.advanced.rest.dto.WrappedResponse
import org.tsdes.dto.TripDto
import org.tsdes.trip.service.TripService
import java.net.URI

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
        val auth = SecurityContextHolder.getContext().authentication
        return tripService.getTripById(tripId).run {
            when {
                this == null -> RestResponseFactory.notFound("no trip found on the id = $tripId")
                auth.principal !is UserDetails -> RestResponseFactory.noPayload(401)
                this.userId != (auth as UserDetails).username -> RestResponseFactory.noPayload(403)
                else -> RestResponseFactory.payload(200, this.toDto())
            }
        }
    }

    @ApiOperation("get all trip for the user")
    @GetMapping
    fun getAllTripsByUser(): ResponseEntity<WrappedResponse<List<TripDto>>> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth.principal !is UserDetails) return RestResponseFactory.noPayload(401)
        if ((auth.principal as UserDetails).username == null) return RestResponseFactory.noPayload(401)
        val username = (auth.principal as UserDetails).username
        return RestResponseFactory.payload(
            200,
            tripService.getTripsByUserId(username).map { it.toDto() })
    }

    @ApiOperation("create a Trip")
    @PostMapping
    fun createTrip(
        @RequestBody dto: TripDto
    ): ResponseEntity<WrappedResponse<Void>> {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth.principal !is UserDetails) return RestResponseFactory.noPayload(401)
        if (dto.userId != (auth.principal as UserDetails).username)
            return RestResponseFactory.userFailure("userId is not your Id")

        return RestResponseFactory.created(URI.create("api/trip/${tripService.createTrip(dto).id}"))
    }
}



