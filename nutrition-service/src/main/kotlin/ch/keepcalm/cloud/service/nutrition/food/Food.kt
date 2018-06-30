package ch.keepcalm.cloud.service.nutrition.food

import com.fasterxml.jackson.annotation.JsonFilter
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.TextIndexed

// Our domain object
// id,name,synonyms,category,kcal,fat,protein
@JsonFilter("foodFilter")
data class Food (

        @Id var id: ObjectId,
//        var id: String = UUID.randomUUID().toString(),
        @TextIndexed
        var name: String? = null,
        var synonyms: String? = null,
        var category: String? = null,
        var kcal: Int? = null,
        var fat: Double? = null,
        var protein: Double? = null
)