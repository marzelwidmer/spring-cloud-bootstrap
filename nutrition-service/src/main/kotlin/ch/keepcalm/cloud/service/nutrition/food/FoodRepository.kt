package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RestResource

//interface FoodRepository : PagingAndSortingRepository<Food, String>{
interface FoodRepository : PagingAndSortingRepository<Food, String> {
    @RestResource(rel = "by-name")
    fun findByName(name: String) : Food

    // findAllBy(TextCriteria criteria, Sort sort)
    // Execute a full-text search and define sorting dynamically
    fun findAllBy(criteria: TextCriteria): List<Food>
}