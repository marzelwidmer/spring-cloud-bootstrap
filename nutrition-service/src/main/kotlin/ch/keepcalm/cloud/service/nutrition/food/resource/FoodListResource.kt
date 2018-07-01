package ch.keepcalm.cloud.service.nutrition.food.resource

import ch.keepcalm.cloud.service.nutrition.food.controller.FoodController
import org.springframework.hateoas.Link
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.UriTemplate
import org.springframework.hateoas.mvc.ControllerLinkBuilder

class FoodListResource(val totalItems: Int, val _embedded: Map<String, List<Any>>) : ResourceSupport(){
    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(FoodController::class.java).getFoods()).withSelfRel())
        add(Link(UriTemplate("/foods{?name}"), "nu:find"))
    }
}