package ch.keepcalm.cloud.service.nutrition

import ch.keepcalm.cloud.service.nutrition.food.FoodController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ApiController {

    @GetMapping (value = ["/"])
    fun getApiIndex() : ResourceSupport {
        val resource = ResourceSupport()
        resource.add(linkTo(methodOn(FoodController::class.java).getFoods()).withRel("foods"))
        resource.add(linkTo(methodOn(ApiController::class.java).getApiIndex()).withSelfRel())
        return resource
    }

}