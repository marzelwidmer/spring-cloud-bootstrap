package ch.keepcalm.cloud.service.nutrition.food

import java.util.*

// Our domain object
// id,name,synonyms,category,kcal,fat,protein
//@JsonFilter("foodFilter")
data class Food (
        var id: String = UUID.randomUUID().toString(),
        var name: String? = null,
        var synonyms: String? = null,
        var category: String? = null,
        var kcal: Int? = null,
        var fat: Double? = null,
        var protein: Double? = null
)