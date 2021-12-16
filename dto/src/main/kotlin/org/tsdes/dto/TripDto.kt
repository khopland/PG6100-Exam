package org.tsdes.dto

import io.swagger.annotations.ApiModelProperty

class TripDto(
    @get:ApiModelProperty("The ID of the Trip")
    var id: Long? = null,

    @get:ApiModelProperty("The userid of the Trip")
    var userId: String = "",

    @get:ApiModelProperty("The departure of the Trip")
    var departure: Long? = null,

    @get:ApiModelProperty("The destination of the Trip")
    var destination: Long? = null,

    @get:ApiModelProperty("The Boat of the Trip")
    var boat: Long? = null,

    @get:ApiModelProperty("The Boat of the Trip")
    var passengers: Int = 0,

    @get:ApiModelProperty("The status of the Trip")
    var status: Status = Status.BOOKED,
)
