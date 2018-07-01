package ch.keepcalm.cloud.service.nutrition

import ch.keepcalm.cloud.service.nutrition.food.domain.Food
import ch.keepcalm.cloud.service.nutrition.food.repository.FoodRepository
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


@Component
@Profile("mlab")
class DevDatabaseLoader(val foodServiceImporter: FoodServiceImporter, val foodRepository: FoodRepository) {

	init {
		println("load database....")
		foodServiceImporter.loadDatabase()
		val count = foodRepository.count()
		println("... found $count records in database after db load")
	}

}

@Service
class FoodServiceImporter(val foodRepository: FoodRepository){

	fun loadDatabase (){
		val foods = cvsToFoodList()
		foods?.map { food -> foodRepository.save(food) }
	}
}


fun cvsToFoodList(): MutableList<Food>? {

	var fileReader: BufferedReader? = null
	var csvToBean: CsvToBean<Food>?

	try {

		fileReader = BufferedReader(InputStreamReader(ClassPathResource("csv/SwissFoodCompData-v5.3.csv").inputStream, "UTF-8"))

		val mappingStrategy = ColumnPositionMappingStrategy<Food>()
		mappingStrategy.setType(Food::class.java)


		mappingStrategy.setColumnMapping("id", "name", "synonyms", "category", "kcal", "fat", "protein")

		csvToBean = CsvToBeanBuilder<Food>(fileReader)
				.withMappingStrategy(mappingStrategy)
				.withSkipLines(1)
				.withIgnoreLeadingWhiteSpace(true)
				.build()

		val foods = csvToBean.parse()

		for (food in foods) {
			println(food)
		}

		return  foods
	} catch (e: Exception) {
		println("Reading CSV Error!")
		e.printStackTrace()
	} finally {
		try {
			fileReader!!.close()
		} catch (e: IOException) {
			println("Closing fileReader/csvParser Error!")
			e.printStackTrace()
		}
	}
	return null
}

