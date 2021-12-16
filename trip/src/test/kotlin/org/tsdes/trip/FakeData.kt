package org.tsdes.trip

import org.tsdes.dto.BoatDto
import org.tsdes.dto.PortDto

object FakeData {
    fun getBoatDTO(): HashMap<Long, BoatDto> =
        hashMapOf<Long, BoatDto>().apply {
            (1..11).forEach { i ->
                this.putIfAbsent(i.toLong(), BoatDto(i.toLong(), "Boat_$i", "ColorLine", i * 2, 10, 1))
            }
        }

    fun getPortDTO(): HashMap<Long, PortDto> =
        hashMapOf<Long, PortDto>().apply {
            (1..11).forEach { i ->
                this.putIfAbsent(i.toLong(), PortDto(i.toLong(), "Port_$i", "sunny"))
            }
        }
}