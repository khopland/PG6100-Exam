package org.tsdes.port

import org.springframework.boot.SpringApplication


fun main() {
    SpringApplication.run(Application::class.java, "--spring.profiles.active=test")
}