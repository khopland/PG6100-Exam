package org.tsdes.trip.dto

import org.tsdes.dto.Status

class StatusCommandDto(
    var status: Status = Status.BOOKED
)