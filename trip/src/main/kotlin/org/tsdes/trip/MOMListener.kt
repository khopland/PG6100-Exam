package org.tsdes.trip

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.tsdes.trip.service.BoatService
import org.tsdes.trip.service.PortService

@Service
class MOMListener(
    private val boatService: BoatService,
    private val portService: PortService
) {
    companion object {
        private val log = LoggerFactory.getLogger(MOMListener::class.java)
    }

    @RabbitListener(queues = ["#{queueBoatUpdate.name}"])
    fun boatUpdate(msg: Long) {
        log.info("updating boat with id $msg")
        boatService.updateOneBoat(msg)
    }

    @RabbitListener(queues = ["#{queueBoatCreate.name}"])
    fun boatCreate(msg: Long) {
        log.info("getting new boat with id $msg")
        boatService.getNewOneBoat(msg)
    }

    @RabbitListener(queues = ["#{queuePortUpdate.name}"])
    fun portUpdate(msg: Long) {
        log.info("updating port with id $msg")
        portService.updateOnePort(msg)
    }

    @RabbitListener(queues = ["#{queuePortCreate.name}"])
    fun portCreate(msg: Long) {
        log.info("getting new port with id $msg")
        portService.getNewOnePort(msg)
    }
}