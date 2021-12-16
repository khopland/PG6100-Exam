package org.tsdes.boat

import org.springframework.amqp.core.TopicExchange
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket


@SpringBootApplication
class Application {

    @Bean
    fun topicExchange(): TopicExchange =
        TopicExchange("boat")


    @Bean
    fun swaggerApi(): Docket =
        Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            .select()
            .paths(PathSelectors.any())
            .build()


    private fun apiInfo(): ApiInfo =
        ApiInfoBuilder()
            .title("API for trips")
            .description("REST service to handle boats")
            .version("1.0")
            .build()

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}