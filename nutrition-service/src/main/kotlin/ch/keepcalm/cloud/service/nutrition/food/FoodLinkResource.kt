package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder

// Resource with link to available foods
class FoodLinkResource(val id: String, val name: String) : ResourceSupport() {

    constructor(f: Food) : this(f.id.toHexString(), f.name!!)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(FoodController::class.java).getFood(id)).withRel("item"))
    }
}