package org.tsdes.trip

import org.springframework.boot.SpringApplication

fun main() {
    SpringApplication.run(Application::class.java, "--spring.profiles.active=test")
}