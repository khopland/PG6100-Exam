package org.tsdes.trip.config

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMq {

    @Bean
    fun topicBoat(): TopicExchange = TopicExchange("boat")

    @Bean
    fun topicPort(): TopicExchange = TopicExchange("port")
    @Bean
    fun topicTrip(): FanoutExchange = FanoutExchange("trip")

    @Bean
    fun queueBoatCreate(): Queue = Queue("boat-create")

    @Bean
    fun queueBoatUpdate(): Queue = Queue("boat-update")

    @Bean
    fun queuePortCreate(): Queue = Queue("port-create")

    @Bean
    fun queuePortUpdate(): Queue = Queue("port-update")

    @Bean
    fun bindingBoatCreate(
        topicBoat: TopicExchange, queueBoatCreate: Queue
    ): Binding = BindingBuilder.bind(queueBoatCreate).to(topicBoat).with("create")

    @Bean
    fun bindingBoatUpdate(
        topicBoat: TopicExchange, queueBoatUpdate: Queue
    ): Binding = BindingBuilder.bind(queueBoatUpdate).to(topicBoat).with("update")

    @Bean
    fun bindingPortCreate(
        topicPort: TopicExchange, queuePortCreate: Queue
    ): Binding = BindingBuilder.bind(queuePortCreate).to(topicPort).with("create")

    @Bean
    fun bindingPortUpdate(
        topicPort: TopicExchange, queuePortUpdate: Queue
    ): Binding = BindingBuilder.bind(queuePortUpdate).to(topicPort).with("update")

}