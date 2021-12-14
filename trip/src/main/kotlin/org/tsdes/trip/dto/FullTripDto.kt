package org.tsdes.trip.dto

import io.swagger.annotations.ApiModelProperty
import org.tsdes.dto.BoatDto
import org.tsdes.dto.PortDto

class FullTripDto(
    @get:ApiModelProperty("The ID of the Trip")
    var id: Long? = null,

    @get:ApiModelProperty("The userid of the Trip")
    var userId: String = "",

    @get:ApiModelProperty("The departure of the Trip")
    var departure: PortDto? = null,

    @get:ApiModelProperty("The destination of the Trip")
    var destination: PortDto? = null,

    @get:ApiModelProperty("The destination of the Trip")
    var boat: BoatDto? = null,
)
