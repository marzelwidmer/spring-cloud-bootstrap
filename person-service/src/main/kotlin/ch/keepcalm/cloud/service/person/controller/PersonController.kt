package ch.keepcalm.cloud.service.person.controller

import ch.keepcalm.cloud.service.person.model.Person
import org.springframework.hateoas.MediaTypes
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api", produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class Personcontroller(){
     val personMap = mutableMapOf<String, Person>()

    @GetMapping(value = ["/persons"])
    fun getAllPersons(): List<Pair<String, Person>> {
        return personMap.toList()
    }

    @GetMapping(value = ["/persons/{id}"])
    fun getPerson(@PathVariable id: String): Person? {
        return personMap.get(id)
    }

    //  http POST :4003/api/persons/ name="Marcel" age:=43
    @PostMapping(value = ["/persons/"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun postPerson(@RequestBody @Valid person: Person){
         personMap.put(UUID.randomUUID().toString(), person)
    }
}