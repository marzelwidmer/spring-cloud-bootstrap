package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.data.mongodb.repository.MongoRepository

//interface FoodRepository : PagingAndSortingRepository<Food, String>{
interface FoodRepository : MongoRepository<Food, String>{
//    @RestResource(rel = "by-name")
//    fun findByName(@Param("name") name: String, pageable: Pageable): Page<Food>
//    fun findByName(@Param("name") name: String): List<Food>
}