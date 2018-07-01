package ch.keepcalm.cloud.service.nutrition.csv

import ch.keepcalm.cloud.service.nutrition.food.domain.Food
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.bson.types.ObjectId
import org.springframework.core.io.ClassPathResource
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.streams.toList

//http://javasampleapproach.com/kotlin/kotlin-how-to-read-write-csv-file-with-opencsv-example
fun main(args: Array<String>?) {
    var fileReader: BufferedReader? = null
    var csvToBean: CsvToBean<CsvFood>?

    try {
        fileReader = BufferedReader(InputStreamReader(ClassPathResource("csv/SwissFoodCompData-v5.3.csv").inputStream, "UTF-8"))

        val mappingStrategy = ColumnPositionMappingStrategy<CsvFood>()
        mappingStrategy.setType(CsvFood::class.java)
        mappingStrategy.setColumnMapping("id", "name", "synonyms", "category", "kcal", "fat", "protein")

        csvToBean = CsvToBeanBuilder<CsvFood>(fileReader)
                .withMappingStrategy(mappingStrategy)
                .withSkipLines(1)
                .withIgnoreLeadingWhiteSpace(true)
                .build()

        var foods: List<Food> = convertCsvFoodToFood(csvToBean!!)!!
        print("Converted items= ${foods.size}")
    } finally {
        try {
            fileReader!!.close()
        } catch (e: IOException) {
            println("Closing fileReader/csvParser Error!")
            e.printStackTrace()
        }
    }
}

fun convertCsvFoodToFood(csvToBean: CsvToBean<CsvFood>?): List<Food>? {
    var foods =   csvToBean?.parse()?.stream()?.map {
        Food(id = ObjectId.get(), name = it.name, synonyms = it.synonyms, category = it.category, kcal = it.kcal, fat = it.fat, protein = it.protein)
    }?.toList()
    return foods
}

data class CsvFood(
        var id: String? = null,
        var name: String? = null,
        var synonyms: String? = null,
        var category: String? = null,
        var kcal: Int? = null,
        var fat: Double? = null,
        var protein: Double? = null
)



