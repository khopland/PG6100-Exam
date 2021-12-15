package org.tsdes.dto

import io.swagger.annotations.ApiModelProperty

class BoatDto(
    @get:ApiModelProperty("The ID of the Boat")
    var id: Long? = null,
    @get:ApiModelProperty("The ID of the Boat")
    var name: String = "",
    @get:ApiModelProperty("The ID of the Boat")
    var builder: String = "",
    @get:ApiModelProperty("The ID of the Boat")
    var numberOfCrew: Int = 0,
    @get:ApiModelProperty("The ID of the Boat")
    var maxPassengers: Int = 0,
    @get:ApiModelProperty("The ID of the Boat")
    var minPassengers: Int = 0,
)