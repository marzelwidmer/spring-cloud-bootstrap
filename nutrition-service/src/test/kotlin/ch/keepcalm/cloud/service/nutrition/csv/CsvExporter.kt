package ch.keepcalm.cloud.service.nutrition.csv

import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.FileWriter
import java.io.IOException
import java.util.*

class Customer {
    var id: String? = null
    var name: String? = null
    var address: String? = null
    var age: Int = 0

    constructor() {}
    constructor(id: String?, name: String?, address: String?, age: Int) {
        this.id = id
        this.name = name
        this.address = address
        this.age = age
    }

    override fun toString(): String {
        return "Customer [id=" + id + ", name=" + name + ", address=" + address + ", age=" + age + "]"
    }
}




private val CSV_HEADER = arrayOf<String>("id", "name", "address", "age")
private val customers = Arrays.asList(
        Customer("1", "Jack Smith", "Massachusetts", 23),
        Customer("2", "Adam Johnson", "New York", 27),
        Customer("3", "Katherin Carter", "Washington DC", 26),
        Customer("4", "Jack London", "Nevada", 33),
        Customer("5", "Jason Bourne", "California", 36))

fun main(args: Array<String>?) {

    var fileWriter: FileWriter? = null
    var csvWriter: CSVWriter? = null
    var beanToCsv: StatefulBeanToCsv<Customer>?

    try {
        fileWriter = FileWriter("customer.csv")

        // write String Array
        csvWriter = CSVWriter(fileWriter,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)

        csvWriter.writeNext(CSV_HEADER)

        for (customer in customers) {
            val data = arrayOf<String>(
                    customer.id!!,
                    customer.name!!,
                    customer.address!!,
                    customer.age.toString())

            csvWriter.writeNext(data)
        }

        println("Write CSV using CSVWriter successfully!")

        fileWriter = FileWriter("customerList.csv")

        // write List of Objects
        val mappingStrategy = ColumnPositionMappingStrategy<Customer>()
        mappingStrategy.setType(Customer::class.java)
        mappingStrategy.setColumnMapping("id", "name", "address", "age")

        beanToCsv = StatefulBeanToCsvBuilder<Customer>(fileWriter)
                .withMappingStrategy(mappingStrategy)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .build()

        beanToCsv.write(customers)

        println("Write CSV using BeanToCsv successfully!")
    } catch (e: Exception) {
        println("Writing CSV error!")
        e.printStackTrace()
    } finally {
        try {
            fileWriter!!.flush()
            fileWriter.close()
            csvWriter!!.close()
        } catch (e: IOException) {
            println("Flushing/closing error!")
            e.printStackTrace()
        }
    }
}
