package ch.keepcalm.cloud.service.nutrition.food

 import ch.keepcalm.cloud.service.nutrition.HalResource
 import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.springframework.hateoas.*
 import org.springframework.hateoas.mvc.ControllerLinkBuilder
 import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.*


// Complete rest controller with list of foods and detail urls

/*
 http://localhost:8080/foods/10533?


Filter enable Food filter in Food.kt
http://localhost:8080/foods/filter/10533?fields=name,fat
http://localhost:8080/foods/hal

 */

@RestController
@ExposesResourceFor(FoodResource::class)
@RequestMapping("/foods", produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class FoodController(val foodService: FoodService, val entityLinks: EntityLinks) {


    @GetMapping
    fun getFoods(): ResponseEntity<Resources<Resource<Food>>> {
        return ok(Resources.wrap(foodService.findAllFoods())
                .also {
                    it.add(entityLinks.linkToCollectionResource(FoodResource::class.java).withSelfRel())
                    it.forEach { foodResource ->
                        foodResource.add(entityLinks.linkToSingleResource(FoodResource::class.java, foodResource.content.id).withSelfRel())
                    }
                })
    }


    @GetMapping(value = ["/{id}"])
    fun getFood(@PathVariable id: String): ResponseEntity<FoodResource> {
        val food = foodService.findOne(id)
        return ResponseEntity.ok(FoodResource(food))
    }



    @GetMapping(value = ["/hal"])
    fun getHalFoods(): ResponseEntity<HalResource> {

        val foodLinkResources = foodService.findAllFoods()!!.map { food: Food ->
            FoodLinkResource(food)
        }
        val foodResource = HalResource()
        foodResource.embedResource("foods", foodLinkResources)
        foodResource.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn<FoodController>(FoodController::class.java).getFoods()).withSelfRel())

        return ok(foodResource)
    }

    //http://localhost:8080/foods/hal/1?fields=fat,kcal,name
    @GetMapping(value = ["/filter/{id}"], params = ["fields"])
    fun getFoodsWithSomeFields(@PathVariable id: String, @RequestParam fields: Array<String>): MappingJacksonValue {
        val food = foodService.findOne(id)

        val wrapper = MappingJacksonValue(food)
        val filterProvider = SimpleFilterProvider().addFilter("foodFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept(*fields))
        wrapper.filters = filterProvider
        return wrapper
    }
}



