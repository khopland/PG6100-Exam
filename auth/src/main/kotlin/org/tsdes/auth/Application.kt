package org.tsdes.auth

import org.springframework.amqp.core.FanoutExchange
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@SpringBootApplication(scanBasePackages = ["org.tsdes"])
class Application {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun fanout(): FanoutExchange = FanoutExchange("user-creation")

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}