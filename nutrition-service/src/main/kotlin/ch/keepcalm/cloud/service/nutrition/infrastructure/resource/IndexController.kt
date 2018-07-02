package ch.keepcalm.cloud.service.nutrition.infrastructure.resource

import ch.keepcalm.cloud.service.nutrition.food.controller.FoodController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {
    @GetMapping(value = ["/"])
    fun getApiIndex() : ResourceSupport {
        val resource = ResourceSupport()
        resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(FoodController::class.java).getFoods()).withRel("foods"))
        resource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(IndexController::class.java).getApiIndex()).withSelfRel())
        return resource
    }
}
