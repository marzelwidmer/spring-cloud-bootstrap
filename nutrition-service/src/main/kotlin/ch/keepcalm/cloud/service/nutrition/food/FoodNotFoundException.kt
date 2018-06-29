package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Food item not found.")
class FoodNotFoundException(id: String) : RuntimeException(id)
