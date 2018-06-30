package ch.keepcalm.cloud.service.nutrition

import ch.keepcalm.cloud.service.nutrition.food.Food
import ch.keepcalm.cloud.service.nutrition.food.FoodRepository
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


@SpringBootApplication
class NutritionService(val foodRepository: FoodRepository) {

    @Bean
    fun init() = CommandLineRunner {
        val count = foodRepository.count()
        println("found $count records in Food database")
    }


    @Configuration
    @Profile("unsecure")
    @EnableWebSecurity
    internal class WebSecurityConfig : WebSecurityConfigurerAdapter() {
        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            // https://stackoverflow.com/questions/48902706/spring-cloud-eureka-with-spring-security/
            http.csrf().disable()
                    .authorizeRequests()
                    .anyRequest().permitAll()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<NutritionService>(*args)
}


@Component
@Profile("loadDB")
class DatabaseLoader(val foodServiceImporter: FoodServiceImporter, val foodRepository: FoodRepository) {

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
