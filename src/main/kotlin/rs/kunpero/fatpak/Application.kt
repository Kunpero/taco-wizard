package rs.kunpero.fatpak

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [JacksonAutoConfiguration::class])
class Application {
    fun main(args: Array<String>) {
        runApplication<Application>(*args)
    }
}