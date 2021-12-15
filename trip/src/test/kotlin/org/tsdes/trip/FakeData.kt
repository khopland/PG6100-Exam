package org.tsdes.trip

import org.tsdes.dto.BoatDto
import org.tsdes.dto.PortDto

object FakeData {
    fun getBoatDTO(): List<BoatDto> {

        return MutableList(10) { index ->
            BoatDto(index.toLong(), "Boat_$index", "ColorLine", index * 2)
        }
    }

    fun getPortDTO(): List<PortDto> {

        return MutableList(10) { index ->
            PortDto(index.toLong(), "Port_$index", "sunny")
        }
    }
}