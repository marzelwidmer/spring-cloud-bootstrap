package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.stereotype.Service

@Service
class FoodService (val foodRepository: FoodRepository){

    fun findAllFoods (): MutableList<Food>? {
        return foodRepository.findAll()
    }

    fun findOne(id: String): Food {
        val food: Food = foodRepository.findById(id).get()
        return food
    }
}