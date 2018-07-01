package ch.keepcalm.cloud.service.nutrition.food.resource

import ch.keepcalm.cloud.service.nutrition.food.controller.FoodController
import ch.keepcalm.cloud.service.nutrition.food.domain.Food
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder

// Resource with self links
class FoodResource(
        var id: String,
        var name: String,
        var synonyms: String? = null,
        var category: String? = null,
        var kcal: Int? = null,
        var fat: Double? = null,
        var protein: Double? = null
) : ResourceSupport() {

    constructor(f: Food) : this(
            f.id.toHexString(), f.name!!, f.synonyms, f.category, f.kcal, f.fat, f.protein
    )

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(FoodController::class.java).getFood(id)).withSelfRel())
    }
}