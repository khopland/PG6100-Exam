package org.tsdes.trip.db

import org.tsdes.dto.Status
import org.tsdes.dto.TripDto
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class Trip(
    @Id
    @GeneratedValue
    var id: Long = 0,

    @get:NotBlank
    var userId: String = "",

    @get:NotNull
    var departure: Long? = null,

    @get:NotNull
    var destination: Long? = null,

    @get:NotNull
    var boat: Long? = null,

    @get:Min(1)
    var passengers: Int = 0 ,

    var status: Status = Status.BOOKED
) {
    fun toDto(): TripDto = TripDto(id, userId, departure, destination, boat, passengers,status)
}