package org.tsdes.dto

import io.swagger.annotations.ApiModelProperty

class PortDto(
    @get:ApiModelProperty("The ID of the Boat")
    var id: Long? = null,

    @get:ApiModelProperty("The ID of the Boat")
    var name: String = "",

    @get:ApiModelProperty("The ID of the Boat")
    var weather: String = "",
)