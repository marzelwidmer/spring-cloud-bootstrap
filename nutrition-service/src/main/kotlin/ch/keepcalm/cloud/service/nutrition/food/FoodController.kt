package ch.keepcalm.cloud.service.nutrition.food

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.slf4j.LoggerFactory
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.Resource
import org.springframework.hateoas.Resources
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.*

@RestController
@ExposesResourceFor(FoodResource::class)
@RequestMapping("/foods", produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class FoodController(val service: FoodService /*, val entityLinks: EntityLinks*/) {

    private final val log = LoggerFactory.getLogger(FoodController::class.java)

    @GetMapping
    fun getFoods(): ResponseEntity<Foods> {
        val foodList: List<FoodLinkResource> = service.findAllFoods().map { food -> FoodLinkResource(food) }
        val totalItems = service.findAllFoods().count()
        val foodResource = Foods(_embedded = mapOf(Pair("nu:foods", foodList)), totalItems = totalItems)
        return ResponseEntity.ok(foodResource)

    }

    //http GET :4002/foods/ name=='Zwiebel'
    @GetMapping(params = ["name"])
    fun findAllFoodsByName(@RequestParam name: String): ResponseEntity<Resources<Resource<FoodLinkResource>>> {
        return ok(Resources.wrap(service.findAllBy(name).map { food ->
            FoodLinkResource(food)
        }
        ))
    }

    @GetMapping(value = ["/{id}"])
    fun getFood(@PathVariable id: String): ResponseEntity<FoodResource> {
        val food = service.findOne(id)
        return ResponseEntity.ok(FoodResource(food))
    }

    //http://localhost:8080/foods/filter/1?fields=fat,kcal,name
    @GetMapping(value = ["/{id}/filter"], params = ["fields"])
    fun getFoodsWithSomeFields(@PathVariable id: String, @RequestParam fields: Array<String>): MappingJacksonValue {
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