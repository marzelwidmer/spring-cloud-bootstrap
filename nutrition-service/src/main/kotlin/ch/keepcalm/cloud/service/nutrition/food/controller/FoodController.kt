package ch.keepcalm.cloud.service.nutrition.food.controller

import ch.keepcalm.cloud.service.nutrition.food.resource.FoodLinkResource
import ch.keepcalm.cloud.service.nutrition.food.resource.FoodListResource
import ch.keepcalm.cloud.service.nutrition.food.resource.FoodResource
import ch.keepcalm.cloud.service.nutrition.food.service.FoodService
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.slf4j.LoggerFactory
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.MediaTypes
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.*

@RestController
@ExposesResourceFor(FoodResource::class)
@RequestMapping("/foods", produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class FoodController(val service: FoodService /*, val entityLinks: EntityLinks*/) {

    private final val log = LoggerFactory.getLogger(FoodController::class.java)

    @GetMapping
    fun getFoods(): ResponseEntity<FoodListResource> {
        val foodList: List<FoodLinkResource> = service.findAllFoods().map { food -> FoodLinkResource(food) }
        val totalItems = service.findAllFoods().count()
        val foodResource = FoodListResource(_embedded = mapOf(Pair("foods", foodList)), totalItems = totalItems)
        log.info("Get all foods is called")
        return ResponseEntity.ok(foodResource)

    }

    //http GET :4002/foods/ name=='Zwiebel'
    @GetMapping(params = ["name"])
    fun findAllFoodsByName(@RequestParam("name", defaultValue = "*") name: String): ResponseEntity<FoodListResource> {
        log.info("Search food with param [${name}] is called")
        val foodList: List<FoodLinkResource> = service.findAllBy(name).map { food -> FoodLinkResource(food) }
        val totalItems = foodList.size
        val foodResource = FoodListResource(_embedded = mapOf(Pair("foods", foodList)), totalItems = totalItems)
        return ResponseEntity.ok(foodResource)

        /*
        return ok(Resources.wrap(service.findAllBy(name).map { food ->
            FoodResource(food) }
        ))*/
    }

    @GetMapping(value = ["/{id}"])
    fun getFood(@PathVariable id: String): ResponseEntity<FoodResource> {
        log.info("Get a food is called with id [${id}]")
        val food = service.findOne(id)
        return ResponseEntity.ok(FoodResource(food))
    }

    //http://localhost:8080/foods/filter/1?fields=fat,kcal,name
    @GetMapping(value = ["/{id}/filter"], params = ["fields"])
    fun getFoodsWithSomeFields(@PathVariable id: String, @RequestParam fields: Array<String>): MappingJacksonValue {
        log.info("Filter food is called for id [${id}] with fields [${fields}]")

        val food = service.findOne(id)

        val wrapper = MappingJacksonValue(food)
        val filterProvider = SimpleFilterProvider().addFilter("foodFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept(*fields))
        wrapper.filters = filterProvider

        return wrapper
    }

/*
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
*/
/*
   //http GET :4002/foods/search/ name=='Zwiebel, roh'
   @GetMapping(value = ["/search"], params = ["name"])
   fun findFoodsByName(@RequestParam name: String):ResponseEntity<FoodResource> {
       val food = foodService.findByName(name)
       return ResponseEntity.ok(FoodResource(food))
   }
*/

}