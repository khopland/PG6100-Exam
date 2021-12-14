package org.tsdes.boat

import org.springframework.boot.SpringApplication


fun main() {
    SpringApplication.run(Application::class.java, "--spring.profiles.active=test")
}