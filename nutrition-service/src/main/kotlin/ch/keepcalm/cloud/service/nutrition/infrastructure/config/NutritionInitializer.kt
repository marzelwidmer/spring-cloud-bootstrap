package ch.keepcalm.cloud.service.nutrition.infrastructure.config

import ch.keepcalm.cloud.service.nutrition.food.domain.Food
import ch.keepcalm.cloud.service.nutrition.food.repository.FoodRepository
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.streams.toList


@Component
class NutritionInitializer(private val repository: FoodRepository) {

    private final val log = LoggerFactory.getLogger(NutritionInitializer::class.java)
    private final val foods = readFoodDatas()

    init {
        databaseConsistencyChecks(repository.count(), foods.size.toLong())
    }

    fun databaseConsistencyChecks(db: Long, csv: Long) = when {
        db == csv -> log.info("MongoDB is up-to-date.")
        db > csv -> {
            log.error("MongoDB have more documents [${repository.count()}]. CSV file have  [${foods.size}] food item - please check CSV file.")
        }
        db < csv -> {
            log.warn("MongoDB are not up-to-date with [ ${repository.count()}] documents. CSV file have [${foods.size}] food item - start re-import document.")
            log.info("Clear  MongoDB")
            repository.deleteAll()
            log.info("Importing ${foods.size} foods into MongoDB… ")
            foods.map { food -> repository.save(food) }
            log.info("Successfully imported ${repository.count()} foods.")
        }
        else -> throw IllegalStateException()
    }


    private final fun readFoodDatas(): List<Food> {
        var fileReader: BufferedReader? = null
        var csvToBean: CsvToBean<CsvFoodItem>?

        try {
            fileReader = BufferedReader(InputStreamReader(ClassPathResource("SwissFoodCompData-v5.3.csv").inputStream, "UTF-8"))
            val mappingStrategy = ColumnPositionMappingStrategy<CsvFoodItem>()
            mappingStrategy.setType(CsvFoodItem::class.java)
            mappingStrategy.setColumnMapping("id", "name", "synonyms", "category", "kcal", "fat", "protein")

            csvToBean = CsvToBeanBuilder<CsvFoodItem>(fileReader)
                    .withMappingStrategy(mappingStrategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()

            val foods: List<Food> = convertCsvFoodItemToFood(csvToBean!!)!!
            log.info("Converting ${foods.size} food items ")

            return foods
        } catch (e: Exception) {
            log.error("Reading CSV Error!" + e.printStackTrace())
         } finally {
            try {
                if (fileReader != null) {
                    fileReader.close()
                }
            } catch (e: IOException) {
                log.error("Closing fileReader/csvParser Error!" + e.printStackTrace())
            }
        }
        return listOf()
    }

    fun convertCsvFoodItemToFood(csvToBean: CsvToBean<CsvFoodItem>?): List<Food>?  =
       csvToBean?.parse()?.stream()?.map {
           Food(id = ObjectId.get(), name = it.name, synonyms = it.synonyms, category = it.category, kcal = it.kcal, fat = it.fat, protein = it.protein)
        }?.toList()

}
class CsvFoodItem(
        var id: String? = null,
        var name: String? = null,
        var synonyms: String? = null,
        var category: String? = null,
        var kcal: Int? = null,
        var fat: Double? = null,
        var protein: Double? = null
)
