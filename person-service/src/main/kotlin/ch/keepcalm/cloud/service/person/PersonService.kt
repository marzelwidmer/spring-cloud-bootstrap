package ch.keepcalm.cloud.service.person

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class PersonService

fun main(args: Array<String>) {
    runApplication<PersonService>(*args)
}