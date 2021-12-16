package org.tsdes.port

import org.springframework.amqp.core.TopicExchange
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@EnableScheduling
@SpringBootApplication
class Application {
    @Bean
    fun topicExchange(): TopicExchange =
        TopicExchange("port")

    @Bean
    fun swaggerApi(): Docket =
        Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            .select()
            .paths(PathSelectors.any())
            .build()


    private fun apiInfo(): ApiInfo = ApiInfoBuilder()
        .title("API for trips")
        .description("REST service to handle trips by users")
        .version("1.0")
        .build()

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}