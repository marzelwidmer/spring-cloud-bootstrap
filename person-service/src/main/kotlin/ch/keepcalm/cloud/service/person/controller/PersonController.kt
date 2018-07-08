package ch.keepcalm.cloud.service.person.controller

import ch.keepcalm.cloud.service.person.model.Person
import org.springframework.hateoas.MediaTypes
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api", produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class Personcontroller() {

    var personList = mutableListOf<Person>(Person(id ="12345678-1234-1234-1234-12345678", name="Foo", age = 32))

    @GetMapping(value = ["/persons"])
    fun getAllPersons(): List<Person> {
        return personList
    }

    // http GET :4003/api/persons/12345678-1234-1234-1234-12345678
    @GetMapping(value = ["/persons/{id}"])
    fun getPerson(@PathVariable id: String): Person? {
        return personList.first { it.id == id }
    }

    //  http POST :4003/api/persons/ name="Marcel" age:=43
    @PostMapping(value = ["/persons/"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun postPerson(@RequestBody @Valid person: Person) {
        personList.add(person)
     }
}