package ch.keepcalm.cloud.service.nutrition.csv

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException


fun main(args: Array<String>?) {

    var fileReader: BufferedReader? = null
    var csvToBean: CsvToBean<Customer>?

    try {
        fileReader = BufferedReader(FileReader("customer.csv"))

        val mappingStrategy = ColumnPositionMappingStrategy<Customer>()
        mappingStrategy.setType(Customer::class.java)
        mappingStrategy.setColumnMapping("id", "name", "address", "age")

        csvToBean = CsvToBeanBuilder<Customer>(fileReader)
                .withMappingStrategy(mappingStrategy)
                .withSkipLines(1)
                .withIgnoreLeadingWhiteSpace(true)
                .build()

        val customers = csvToBean.parse()
        for (customer in customers) {
            println(customer)
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

