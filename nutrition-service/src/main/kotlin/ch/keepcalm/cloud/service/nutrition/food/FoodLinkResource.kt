package ch.keepcalm.cloud.service.nutrition.food

import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder

// Resource with link to available foods
@JsonRootName(value = "rootname")
@JsonTypeName("rootname")
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT,use= JsonTypeInfo.Id.NAME)

class FoodLinkResource(val id: String, val name: String) : ResourceSupport() {

    constructor(f: Food) : this(f.id.toHexString(), f.name!!)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(FoodController::class.java).getFood(id)).withRel("item"))
    }
}