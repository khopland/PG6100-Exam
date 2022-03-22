package org.tsdes.boat.db

import org.tsdes.dto.BoatDto
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Entity
class Boat(
    @Id
    @GeneratedValue
    var id: Long = 0,
    @get:NotBlank(message = "Boat must have a name")
    var name: String = "",
    @get:NotBlank(message = "Boat must have a builder")
    var builder: String = "",
    @get:Min(1)
    var numberOfCrew: Int = 0,
    @get:Min(1)
    var maxPassengers: Int = 0,
    @get:Min(1)
    var minPassengers: Int = 0
)

fun Boat.toDto(): BoatDto = BoatDto(id, name, builder, numberOfCrew,maxPassengers,minPassengers)