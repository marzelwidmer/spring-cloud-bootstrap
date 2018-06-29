package ch.keepcalm.cloud.service.nutrition.csv

import ch.keepcalm.cloud.service.nutrition.food.Food
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


//http://javasampleapproach.com/kotlin/kotlin-how-to-read-write-csv-file-with-opencsv-example
fun main(args: Array<String>?) {

    var fileReader: BufferedReader? = null
    var csvToBean: CsvToBean<Food>?

    try {
        fileReader = BufferedReader(FileReader("src/main/resources/csv/SwissFoodCompData-v5.3.csv"))

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
}


