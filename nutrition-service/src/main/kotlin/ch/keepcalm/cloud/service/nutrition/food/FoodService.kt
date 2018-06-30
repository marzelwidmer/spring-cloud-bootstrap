package ch.keepcalm.cloud.service.nutrition.food

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

//    fun findByName(name: String) = foodRepository.findByName(name, PageRequest.of(0, 10))
    fun findByName(name: String) = foodRepository.findOne()

}