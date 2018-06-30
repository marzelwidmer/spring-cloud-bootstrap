package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.stereotype.Service



@Service
class FoodService (private val foodRepository: FoodRepository){

    fun findAllFoods (): MutableIterable<Food> {
        return foodRepository.findAll()
    }

    fun findOne(id: String): Food {
        val food: Food = foodRepository.findById(id).get()
        return food
    }

    fun findByName(name: String) =foodRepository.findByName(name)
    fun findAllBy(name: String) =foodRepository.findAllBy(TextCriteria.forDefaultLanguage().matchingAny(name))
}