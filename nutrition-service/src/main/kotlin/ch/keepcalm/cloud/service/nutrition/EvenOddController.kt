package ch.keepcalm.cloud.service.nutrition


import ch.keepcalm.cloud.service.nutrition.food.service.FoodService
import org.springframework.hateoas.MediaTypes
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping( produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class EvenOddController(val service: FoodService) {

    @GetMapping("/validate/prime-number")
    fun isNumberPrime(@RequestParam("number") number: Int?): String {
        return if (number!! % 2 == 0) "Even" else "Odd"
    }
}
