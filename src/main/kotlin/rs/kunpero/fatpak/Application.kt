package rs.kunpero.fatpak

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration

@SpringBootApplication(exclude = [JacksonAutoConfiguration::class])
open class Application {
    fun main(args: Array<String>) {
        SpringApplication.run(Application::class.java, *args)
    }
}