package ch.keepcalm.cloud.service.nutrition.food

import org.springframework.data.mongodb.repository.MongoRepository

interface FoodRepository : MongoRepository<Food, String>