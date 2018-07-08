package ch.keepcalm.cloud.service.person.model

data class Person(var name: String? = null,
                  var age: Int? = null)
                 // var address: Address? = null)

//
//data class Address(var street: String? = null,
//                   var number: Int? = null,
//                   var city: String? = null)
//
//
//fun person(block: (Person) -> Unit): Person {
//    val p = Person()
//    block(p)
//    return p
//}
