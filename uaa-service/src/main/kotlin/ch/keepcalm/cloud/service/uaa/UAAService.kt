package ch.keepcalm.cloud.service.uaa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UAAService

fun main(args: Array<String>) {
    runApplication<UAAService>(*args)
}