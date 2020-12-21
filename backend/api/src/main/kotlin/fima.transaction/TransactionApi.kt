package fima.transaction

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan

@EnableAutoConfiguration
@ComponentScan
open class Application

fun main(args: Array<String>) {
    val app = SpringApplication(Application::class.java)
    app.webApplicationType = WebApplicationType.REACTIVE
    app.run(*args)
}