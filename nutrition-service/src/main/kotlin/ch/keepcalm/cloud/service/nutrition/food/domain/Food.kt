package ch.keepcalm.cloud.service.nutrition.food.domain

import com.fasterxml.jackson.annotation.JsonFilter
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed
import org.springframework.data.mongodb.core.mapping.Document

// Our domain object
// id,name,synonyms,category,kcal,fat,protein
@JsonFilter("foodFilter")

@Document
data class Food (
        @Id var id: ObjectId,
        @TextIndexed(weight = 2f)
        var name: String? = null,
        @TextIndexed(weight = 3f)
        var synonyms: String? = null,
        var category: String? = null,
        var kcal: Int? = null,
        var fat: Double? = null,
        var protein: Double? = null
)