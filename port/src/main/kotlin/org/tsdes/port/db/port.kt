package org.tsdes.port.db

import org.tsdes.dto.PortDto
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Port(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @get:NotBlank(message = "port must have a name")
    var name: String = "",
    @get:NotBlank(message = "port must have a whether")
    var weather: String = ""
)

fun Port.toDto(): PortDto {
    return PortDto(id, name, weather)
}